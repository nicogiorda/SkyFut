package simulador.repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.persistence.DatabaseConnection;
import simulador.strategy.TacticaDefensiva;
import simulador.strategy.TacticaEquilibrada;
import simulador.strategy.TacticaOfensiva;
import simulador.strategy.TacticaStrategy;

/**
 * [PATRON: Repository/DAO — DAO de Equipo]
 *
 * Que hace: Encapsula todo el acceso a datos de la tabla equipo y tactica para
 * el dominio. Carga el plantel completo de cada equipo delegando en RepositorioJugador
 * y construye el objeto TacticaStrategy correcto segun el nombre en BD.
 * Es la unica clase que sabe como mapear una fila de equipo+tactica a un objeto Equipo
 * con su List<IJugador> de titulares y suplentes.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Agregacion con: RepositorioJugador repositorioJugador (colaborador para cargar plantel),
 *   Connection connection (obtenida del Singleton DatabaseConnection)
 * - Usada por (dependencia): TorneoFacade (para cargar y seleccionar equipos),
 *   RepositorioPartido (para reconstruir Partido con sus Equipos completos)
 * - Crea (Creator GRASP): Equipo (con id, nombre, titulares, suplentes y tactica),
 *   TacticaStrategy concretas (TacticaDefensiva, Ofensiva, Equilibrada)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque es la unica clase responsable del acceso a BD
 *   para equipos; ninguna otra clase ejecuta SQL de equipo directamente.
 */
public class RepositorioEquipo {
    private final Connection connection;
    private final RepositorioJugador repositorioJugador;

    public RepositorioEquipo() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener la conexion a la base de datos", e);
        }
        this.repositorioJugador = new RepositorioJugador();
    }

    public List<Equipo> listarEquipos() {
        String sql = """
                SELECT e.id, e.nombre, t.nombre AS tactica
                FROM equipo e
                JOIN tactica t ON t.id = e.id_tactica
                ORDER BY e.nombre
                """;

        List<Equipo> equipos = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                equipos.add(mapearEquipoConPlantel(rs));
            }
            return equipos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los equipos", e);
        }
    }

    public Optional<Equipo> buscarPorId(int idEquipo) {
        String sql = """
                SELECT e.id, e.nombre, t.nombre AS tactica
                FROM equipo e
                JOIN tactica t ON t.id = e.id_tactica
                WHERE e.id = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEquipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEquipoConPlantel(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar el equipo " + idEquipo, e);
        }
    }

    private Equipo mapearEquipoConPlantel(ResultSet rs) throws SQLException {
        int idEquipo = rs.getInt("id");
        List<IJugador> titulares = repositorioJugador.listarPorEquipoYRol(idEquipo, "TITULAR");
        List<IJugador> suplentes = repositorioJugador.listarPorEquipoYRol(idEquipo, "SUPLENTE");
        return new Equipo(
                idEquipo,
                rs.getString("nombre"),
                titulares,
                suplentes,
                crearTactica(rs.getString("tactica")));
    }

    private TacticaStrategy crearTactica(String nombreTactica) {
        return switch (nombreTactica) {
            case "Defensiva" -> new TacticaDefensiva();
            case "Ofensiva" -> new TacticaOfensiva();
            default -> new TacticaEquilibrada();
        };
    }
}
