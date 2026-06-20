package simulador.dto;

/**
 * [PATRON: (ninguno) — DTO inmutable (Java record)]
 *
 * Que hace: Transporta las estadisticas acumuladas de un jugador a lo largo de
 * todo el torneo para su presentacion en la UI (panel "Consultar Estadisticas").
 * Agrega partidos jugados, goles, asistencias, tarjetas, lesiones, minutos y
 * rendimiento promedio. Al ser record, es inmutable y generado directamente
 * desde una consulta SQL con GROUP BY en RepositorioPartido.
 *
 * Relaciones:
 * - Hereda de: (Java record — extiende implicitamente java.lang.Record)
 * - Composicion con: (ninguna)
 * - Asociacion con: (ninguna — solo tipos primitivos y String)
 * - Usada por (dependencia): RepositorioPartido.listarEstadisticasEquipo() (la crea),
 *   GestorTorneo.consultarEstadisticasEquipo() (la propaga),
 *   TorneoFacade.consultarEstadisticasEquipoDt() (la retorna),
 *   SkyFutFrame (la muestra en la tabla de estadisticas)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque la UI recibe datos planos (strings y numeros)
 *   sin necesitar conocer IJugador, Equipo ni ninguna clase de dominio.
 */
public record EstadisticaJugadorTorneo(
        String jugador,
        String posicion,
        int partidos,
        int goles,
        int asistencias,
        int tarjetasAmarillas,
        int lesiones,
        int minutosJugados,
        double rendimientoPromedio) {
}
