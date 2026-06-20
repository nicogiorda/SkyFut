package simulador.dto;

/**
 * [PATRON: (ninguno) — DTO inmutable (Java record)]
 *
 * Que hace: Transporta los datos de un partido del fixture para su representacion
 * grafica en el cuadro del torneo: fase, orden, equipos, marcador, ganador y estado.
 * Al ser un record Java, es automaticamente inmutable y tiene equals/hashCode/toString.
 * El metodo finalizado() es un helper de conveniencia para la UI.
 *
 * Relaciones:
 * - Hereda de: (Java record — extiende implicitamente java.lang.Record)
 * - Composicion con: (ninguna)
 * - Asociacion con: (ninguna — solo tipos primitivos y Strings)
 * - Usada por (dependencia): TorneoFacade.consultarFixture() (la retorna en lista),
 *   RepositorioPartido.listarFixture() (la crea), FixturePanel (la dibuja),
 *   SkyFutFrame (la pasa a FixturePanel via JDialog)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque FixturePanel solo depende de este DTO y no
 *   de ninguna clase de dominio (Partido, Equipo, Fase), manteniendo la UI aislada.
 */
public record FixturePartido(
        int faseOrden,
        String faseNombre,
        int partidoId,
        int ordenPartido,
        String equipoLocal,
        String equipoVisitante,
        int golesLocal,
        int golesVisitante,
        String ganador,
        String estado) {

    public boolean finalizado() {
        return "FINALIZADO".equals(estado);
    }
}
