package simulador.strategy;

/**
 * [PATRON: Strategy — ConcreteStrategy]
 *
 * Que hace: Implementa la tactica ofensiva con modificador de ataque 1.2 (+20%)
 * y modificador de defensa 0.85 (-15%). Favorece el ataque a costa de la defensa.
 * Creada por RepositorioEquipo al cargar equipos desde BD, o por SkyFutFrame
 * cuando el DT selecciona esta tactica durante el entretiempo.
 *
 * Relaciones:
 * - Implementa: TacticaStrategy
 * - Composicion con: (ninguna)
 * - Asociacion con: (ninguna)
 * - Usada por (dependencia): Equipo (la tiene asignada como tactica activa),
 *   RepositorioEquipo (la instancia segun datos de BD),
 *   SkyFutFrame (la instancia cuando el DT la selecciona)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Polimorfismo: cumple porque implementa TacticaStrategy con valores especificos
 *   para el estilo ofensivo, sin que el cliente (Equipo, ContextoEvento) necesite
 *   conocer que tipo de tactica esta usando.
 */
public class TacticaOfensiva implements TacticaStrategy {
    @Override
    public double getModificadorAtaque() {
        return 1.2;
    }

    @Override
    public double getModificadorDefensa() {
        return 0.85;
    }

    @Override
    public String getFormacion() {
        return "Ofensiva";
    }
}
