package simulador.repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import simulador.domain.IJugador;
import simulador.domain.Jugador;
import simulador.persistence.DatabaseConnection;

/**
 * [PATRON: Repository/DAO — DAO de Jugador]
 *
 * Que hace: Encapsula todo el acceso a datos de la tabla jugador (y su union con
 * plantel). listarPorEquipoYRol() hace el JOIN jugador+plantel filtrando por equipo
 * y rol_inicial (TITULAR/SUPLENTE), ordenando por numero_dorsal. Crea objetos Jugador
 * (ConcreteComponent del Decorator) para poblar las listas de Equipo.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Agregacion con: Connection connection (obtenida del Singleton DatabaseConnection)
 * - Usada por (dependencia): RepositorioEquipo (la llama para cargar titulares
 *   y suplentes de cada equipo al construir un Equipo)
 * - Crea (Creator GRASP): Jugador (instancias con id, nombre, posicion, rendimientoBase)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque es la unica clase responsable del acceso a BD
 *   para jugadores; RepositorioEquipo la delega en lugar de duplicar el SQL.
 */
public class RepositorioJugador {
    private final Connection connection;

    public RepositorioJugador() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener la conexion a la base de datos", e);
        }
    }

    public List<IJugador> listarPorEquipo(int idEquipo) {
        String sql = """
                SELECT j.id, j.nombre, j.posicion, j.rendimiento_base
                FROM jugador j
                WHERE j.id_equipo = ?
                ORDER BY j.id
                """;

        List<IJugador> jugadores = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEquipo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jugadores.add(mapearJugador(rs));
                }
            }
            return jugadores;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los jugadores del equipo " + idEquipo, e);
        }
    }

    public List<IJugador> listarPorEquipoYRol(int idEquipo, String rolInicial) {
        String sql = """
                SELECT j.id, j.nombre, j.posicion, j.rendimiento_base
                FROM jugador j
                JOIN plantel p ON p.id_jugador = j.id
                WHERE p.id_equipo = ?
                  AND p.rol_inicial = ?
                ORDER BY p.numero_dorsal, j.id
                """;

        List<IJugador> jugadores = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEquipo);
            stmt.setString(2, rolInicial);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jugadores.add(mapearJugador(rs));
                }
            }
            return jugadores;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los jugadores del equipo " + idEquipo, e);
        }
    }

    private IJugador mapearJugador(ResultSet rs) throws SQLException {
        return new Jugador(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("posicion"),
                rs.getDouble("rendimiento_base"));
    }
}
