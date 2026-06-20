package simulador.strategy;

/**
 * [PATRON: Strategy — ConcreteStrategy]
 *
 * Que hace: Implementa la tactica equilibrada con modificadores de ataque 1.0 y
 * defensa 1.0 (sin modificacion). Es la tactica neutral que no altera el rendimiento
 * base del equipo. Usada como valor por defecto cuando la tactica de la BD no
 * coincide con Ofensiva ni Defensiva en RepositorioEquipo.
 *
 * Relaciones:
 * - Implementa: TacticaStrategy
 * - Composicion con: (ninguna)
 * - Asociacion con: (ninguna)
 * - Usada por (dependencia): Equipo (la tiene asignada como tactica activa),
 *   RepositorioEquipo (la instancia como caso por defecto en crearTactica()),
 *   SkyFutFrame (la instancia cuando el DT la selecciona)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Polimorfismo: cumple porque implementa TacticaStrategy con valores neutrales,
 *   siendo intercambiable con TacticaOfensiva y TacticaDefensiva.
 */
public class TacticaEquilibrada implements TacticaStrategy {
    @Override
    public double getModificadorAtaque() {
        return 1.0;
    }

    @Override
    public double getModificadorDefensa() {
        return 1.0;
    }

    @Override
    public String getFormacion() {
        return "Equilibrada";
    }
}
