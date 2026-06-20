package simulador.strategy;

/**
 * [PATRON: Strategy — ConcreteStrategy]
 *
 * Que hace: Implementa la tactica defensiva con modificador de ataque 0.85 (-15%)
 * y modificador de defensa 1.2 (+20%). Prioriza la solidez defensiva a costa
 * de la capacidad ofensiva. Creada por RepositorioEquipo o por SkyFutFrame.
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
 *   para el estilo defensivo, de forma intercambiable con las otras estrategias.
 */
public class TacticaDefensiva implements TacticaStrategy {
    @Override
    public double getModificadorAtaque() {
        return 0.85;
    }

    @Override
    public double getModificadorDefensa() {
        return 1.2;
    }

    @Override
    public String getFormacion() {
        return "Defensiva";
    }
}
