package simulador.repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import simulador.dto.EquipoFixture;
import simulador.persistence.DatabaseConnection;

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
            throw new IllegalStateException("No se pudieron guardar los partidos iniciales", e);
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
}
