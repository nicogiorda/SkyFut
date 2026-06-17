package simulador.repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.domain.EstadisticasJugador;
import simulador.dto.Goleador;
import simulador.dto.ResumenTorneo;
import simulador.dto.ResultadoPartido;
import simulador.events.Cambio;
import simulador.events.EventoPartido;
import simulador.events.Gol;
import simulador.events.Lesion;
import simulador.events.Tarjeta;
import simulador.persistence.DatabaseConnection;

public class RepositorioPartido {
    private final Connection connection;
    private final RepositorioEquipo repositorioEquipo;

    public RepositorioPartido(RepositorioEquipo repositorioEquipo) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener la conexion a la base de datos", e);
        }
        this.repositorioEquipo = repositorioEquipo;
    }

    public Optional<Partido> buscarSiguientePartidoNoIniciado() {
        String sql = "SELECT id, id_equipo_local, id_equipo_visit FROM partido WHERE estado = 'NO_INICIADO' ORDER BY id LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) return Optional.empty();
            int id = rs.getInt("id");
            Equipo local = repositorioEquipo.buscarPorId(rs.getInt("id_equipo_local"))
                    .orElseThrow(() -> new IllegalStateException("No se encontró equipo local"));
            Equipo visitante = repositorioEquipo.buscarPorId(rs.getInt("id_equipo_visit"))
                    .orElseThrow(() -> new IllegalStateException("No se encontró equipo visitante"));
            return Optional.of(new Partido(id, local, visitante));
        } catch (SQLException e) {
            throw new IllegalStateException("Error al buscar siguiente partido", e);
        }
    }

    public void guardarResultado(Partido partido) {
        String sql = "UPDATE partido SET goles_local = ?, goles_visitante = ?, id_ganador = ?, estado = 'FINALIZADO' WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, partido.getGolesLocal());
            stmt.setInt(2, partido.getGolesVisitante());
            Equipo ganador = partido.getGanador();
            if (ganador != null) {
                stmt.setInt(3, ganador.getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, partido.getId());
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("Error al guardar resultado del partido", e);
        }
    }

    public void guardarEventos(Partido partido) {
        String sql = """
                INSERT INTO evento_partido
                (id_partido, tipo, minuto, id_jugador_principal, id_jugador_secundario, id_equipo, descripcion)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (EventoPartido e : partido.getEventos()) {
                stmt.setInt(1, partido.getId());
                stmt.setString(2, e.getTipo());
                stmt.setInt(3, e.getMinuto());
                stmt.setString(7, e.getDescripcion());
                setParametrosEvento(stmt, e);
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("Error al guardar eventos del partido", e);
        }
    }

    private void setParametrosEvento(PreparedStatement stmt, EventoPartido evento) throws SQLException {
        // params 4=id_jugador_principal, 5=id_jugador_secundario, 6=id_equipo
        if (evento instanceof Gol gol) {
            stmt.setInt(4, gol.getAutor().getId());
            stmt.setNull(5, Types.INTEGER);
            stmt.setInt(6, gol.getEquipo().getId());
        } else if (evento instanceof Tarjeta t) {
            stmt.setInt(4, t.getJugador().getId());
            stmt.setNull(5, Types.INTEGER);
            stmt.setInt(6, t.getEquipo().getId());
        } else if (evento instanceof Lesion l) {
            stmt.setInt(4, l.getJugador().getId());
            stmt.setNull(5, Types.INTEGER);
            stmt.setInt(6, l.getEquipo().getId());
        } else if (evento instanceof Cambio c) {
            stmt.setInt(4, c.getSale().getId());
            stmt.setInt(5, c.getEntra().getId());
            stmt.setInt(6, c.getEquipo().getId());
        } else {
            stmt.setNull(4, Types.INTEGER);
            stmt.setNull(5, Types.INTEGER);
            stmt.setNull(6, Types.INTEGER);
        }
    }

    public void guardarEstadisticas(int idPartido, List<EstadisticasJugador> estadisticas) {
        String sql = """
                INSERT OR REPLACE INTO estadistica_jugador
                (id_partido, id_jugador, id_equipo, goles, asistencias,
                 tarjetas_amarillas, tarjeta_roja, lesionado, minutos_jugados, rendimiento_final)
                VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (EstadisticasJugador stats : estadisticas) {
                stmt.setInt(1, idPartido);
                stmt.setInt(2, stats.getJugador().getId());
                stmt.setInt(3, stats.getIdEquipo());
                stmt.setInt(4, stats.getGoles());
                stmt.setInt(5, stats.getAsistencias());
                stmt.setInt(6, stats.getTarjetas());
                stmt.setInt(7, stats.isLesionado() ? 1 : 0);
                stmt.setInt(8, stats.getMinutosJugados());
                stmt.setDouble(9, stats.getRendimientoFinal());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("Error al guardar estadísticas del partido", e);
        }
    }

    public void actualizarEstadoJugadorTorneo(int idTorneo, EstadisticasJugador stats) {
        String sql = """
                INSERT INTO estado_jugador_torneo
                (id_torneo, id_jugador, cansancio, lesionado, tarjetas_amarillas_acum, suspendido)
                VALUES (?, ?, ?, ?, ?, 0)
                ON CONFLICT(id_torneo, id_jugador) DO UPDATE SET
                  cansancio = excluded.cansancio,
                  lesionado = MAX(lesionado, excluded.lesionado),
                  tarjetas_amarillas_acum = tarjetas_amarillas_acum + excluded.tarjetas_amarillas_acum
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            stmt.setInt(2, stats.getJugador().getId());
            stmt.setDouble(3, Math.max(0.0, 1.0 - stats.getRendimientoFinal()));
            stmt.setInt(4, stats.isLesionado() ? 1 : 0);
            stmt.setInt(5, stats.getTarjetas());
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback();
            throw new IllegalStateException("Error al actualizar estado del jugador en torneo", e);
        }
    }

    public ResumenTorneo consultarResumen(int idTorneo, String nombreTorneo) {
        List<ResultadoPartido> resultados = listarResultados(idTorneo);
        List<Goleador> goleadores = listarGoleadores(idTorneo);
        boolean completo = !resultados.isEmpty() &&
                resultados.stream().allMatch(r -> "FINALIZADO".equals(r.getEstado()));
        return new ResumenTorneo(nombreTorneo, resultados, goleadores, completo);
    }

    private List<ResultadoPartido> listarResultados(int idTorneo) {
        String sql = """
                SELECT p.goles_local, p.goles_visitante, p.estado,
                       el.nombre AS local, ev.nombre AS visitante,
                       eg.nombre AS ganador
                FROM partido p
                JOIN fase f ON p.id_fase = f.id
                JOIN equipo el ON p.id_equipo_local = el.id
                JOIN equipo ev ON p.id_equipo_visit = ev.id
                LEFT JOIN equipo eg ON p.id_ganador = eg.id
                WHERE f.id_torneo = ?
                ORDER BY p.id
                """;
        List<ResultadoPartido> resultados = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(new ResultadoPartido(
                            rs.getString("local"),
                            rs.getString("visitante"),
                            rs.getInt("goles_local"),
                            rs.getInt("goles_visitante"),
                            rs.getString("ganador"),
                            rs.getString("estado")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar resultados", e);
        }
        return resultados;
    }

    private List<Goleador> listarGoleadores(int idTorneo) {
        String sql = """
                SELECT j.nombre AS jugador, eq.nombre AS equipo, SUM(sj.goles) AS total
                FROM estadistica_jugador sj
                JOIN jugador j ON sj.id_jugador = j.id
                JOIN equipo eq ON sj.id_equipo = eq.id
                JOIN partido p ON sj.id_partido = p.id
                JOIN fase f ON p.id_fase = f.id
                WHERE f.id_torneo = ? AND sj.goles > 0
                GROUP BY j.id, eq.id
                ORDER BY total DESC
                """;
        List<Goleador> goleadores = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTorneo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goleadores.add(new Goleador(
                            rs.getString("jugador"),
                            rs.getString("equipo"),
                            rs.getInt("total")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar goleadores", e);
        }
        return goleadores;
    }

    private void rollback() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }
}
