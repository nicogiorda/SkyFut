package simulador.repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import simulador.dto.EquipoFixture;
import simulador.persistence.DatabaseConnection;

/**
 * [PATRON: Repository/DAO — DAO de Torneo y Fase]
 *
 * Que hace: Encapsula todo el acceso a datos de las tablas torneo y fase.
 * Crea torneos y fases, asigna el equipo DT, guarda los partidos iniciales de
 * cada fase, consulta si una fase esta completa, lista ganadores y crea la siguiente
 * fase. El record interno FaseInfo es una proyeccion minima (id, nombre, orden).
 * Realiza rollback ante cualquier SQLException. Toda la logica de "cual es la
 * siguiente fase" esta en GestorFases; este repositorio solo persiste.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Agregacion con: Connection connection (Singleton DatabaseConnection)
 * - Usada por (dependencia): GestorTorneo (para crear torneo y fase inicial),
 *   GestorFases (para verificar completitud, marcar completa, crear siguiente fase)
 * - Crea (Creator GRASP): EquipoFixture (en listarEquiposConPlantelCompleto y
 *   listarGanadoresFase), FaseInfo (record interno, proyeccion de fase)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque centraliza todo el SQL de torneo/fase;
 *   GestorTorneo y GestorFases no necesitan conocer el esquema de BD.
 */
public class RepositorioTorneo {
    private final Connection connection;

    public RepositorioTorneo() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener la conexion a la base de datos", e);
        }
    }

    public int crearTorneo(String nombre) {
        try {
            String sql = "INSERT INTO torneo (nombre, fecha_inicio, estado) VALUES (?, ?, 'EN_CURSO')";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, nombre);
                stmt.setString(2, LocalDate.now().toString());
                stmt.executeUpdate();
                int id = leerIdGenerado(stmt, "torneo");
                connection.commit();
                return id;
            }
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("No se pudo crear el torneo", e);
        }
    }

    public int crearFase(int idTorneo, String nombre, int orden) {
        try {
            String sql = "INSERT INTO fase (id_torneo, nombre, orden, completada) VALUES (?, ?, ?, 0)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, idTorneo);
                stmt.setString(2, nombre);
                stmt.setInt(3, orden);
                stmt.executeUpdate();
                int id = leerIdGenerado(stmt, "fase");
                connection.commit();
                return id;
            }
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("No se pudo crear la fase", e);
        }
    }

    public void asignarEquipoDT(int idTorneo, int idEquipo) {
        String sql = "UPDATE torneo SET id_equipo_dt = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEquipo);
            stmt.setInt(2, idTorneo);
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("No se pudo asignar el equipo del DT", e);
        }
    }

    public boolean faseTienePartidos(int idFase) {
        String sql = "SELECT COUNT(*) AS total FROM partido WHERE id_fase = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idFase);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar los partidos de la fase", e);
        }
    }

    public boolean faseCompleta(int idFase) {
        String sql = """
                SELECT COUNT(*) AS pendientes
                FROM partido
                WHERE id_fase = ?
                  AND estado <> 'FINALIZADO'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idFase);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("pendientes") == 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar si la fase esta completa", e);
        }
    }

    public void marcarFaseCompleta(int idFase) {
        String sql = "UPDATE fase SET completada = 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idFase);
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("No se pudo marcar la fase como completa", e);
        }
    }

    public Optional<FaseInfo> buscarFaseDePartido(int idPartido) {
        String sql = """
                SELECT f.id, f.nombre, f.orden
                FROM fase f
                JOIN partido p ON p.id_fase = f.id
                WHERE p.id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idPartido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new FaseInfo(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("orden")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar la fase del partido", e);
        }
    }

    public List<EquipoFixture> listarEquiposConPlantelCompleto(int cantidad) {
        String sql = """
                SELECT e.id, e.id_tactica
                FROM equipo e
                WHERE (
                    SELECT COUNT(*)
                    FROM plantel p
                    WHERE p.id_equipo = e.id
                ) >= 18
                ORDER BY e.id
                LIMIT ?
                """;
        List<EquipoFixture> equipos = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipos.add(new EquipoFixture(rs.getInt("id"), rs.getInt("id_tactica")));
                }
            }
            return equipos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los equipos del torneo", e);
        }
    }

    public List<EquipoFixture> listarGanadoresFase(int idFase) {
        String sql = """
                SELECT e.id, e.id_tactica
                FROM partido p
                JOIN equipo e ON e.id = p.id_ganador
                WHERE p.id_fase = ?
                ORDER BY p.id
                """;
        List<EquipoFixture> ganadores = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idFase);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ganadores.add(new EquipoFixture(rs.getInt("id"), rs.getInt("id_tactica")));
                }
            }
            return ganadores;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los ganadores de la fase", e);
        }
    }

    public boolean faseExiste(int idTorneo, int orden) {
        String sql = "SELECT COUNT(*) AS total FROM fase WHERE id_torneo = ? AND orden = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            stmt.setInt(2, orden);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar si la fase existe", e);
        }
    }

    public void guardarPartidosIniciales(int idFase, List<EquipoFixture> equiposOrdenados) {
        if (equiposOrdenados.size() % 2 != 0) {
            throw new IllegalArgumentException("La cantidad de equipos debe ser par");
        }

        String sql = """
                INSERT INTO partido (
                    id_fase,
                    id_equipo_local,
                    id_equipo_visit,
                    estado,
                    id_tactica_local,
                    id_tactica_visit
                )
                VALUES (?, ?, ?, 'NO_INICIADO', ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < equiposOrdenados.size(); i += 2) {
                EquipoFixture local = equiposOrdenados.get(i);
                EquipoFixture visitante = equiposOrdenados.get(i + 1);
                stmt.setInt(1, idFase);
                stmt.setInt(2, local.idEquipo());
                stmt.setInt(3, visitante.idEquipo());
                stmt.setInt(4, local.idTactica());
                stmt.setInt(5, visitante.idTactica());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("No se pudieron guardar los partidos de la fase", e);
        }
    }

    public void finalizarTorneo(int idTorneo, int idCampeon) {
        String sql = "UPDATE torneo SET estado = 'FINALIZADO', id_campeon = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCampeon);
            stmt.setInt(2, idTorneo);
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("No se pudo finalizar el torneo", e);
        }
    }

    public Optional<String> buscarNombreCampeon(int idTorneo) {
        String sql = """
                SELECT e.nombre AS campeon
                FROM torneo t
                JOIN equipo e ON e.id = t.id_campeon
                WHERE t.id = ?
                  AND t.estado = 'FINALIZADO'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(rs.getString("campeon"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el campeon del torneo", e);
        }
    }

    private int leerIdGenerado(Statement stmt, String entidad) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo obtener el id generado para " + entidad);
    }

    private void rollback() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    public record FaseInfo(int id, String nombre, int orden) {
    }
}
