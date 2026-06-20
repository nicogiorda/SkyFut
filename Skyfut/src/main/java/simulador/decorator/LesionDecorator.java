package simulador.decorator;

import simulador.domain.IJugador;

/**
 * [PATRON: Decorator — ConcreteDecorator]
 *
 * Que hace: Penaliza severamente el rendimiento de un jugador lesionado multiplicando
 * su rendimiento por 0.30 (reduccion del 70%). Representa que el jugador sigue en
 * cancha pero gravemente limitado. Se aplica cuando ocurre un evento Lesion.
 *
 * Relaciones:
 * - Hereda de: JugadorDecorator
 * - Composicion con: (ninguna adicional a la del padre)
 * - Asociacion con: (ninguna)
 * - Usada por (dependencia): Lesion.aplicar() (la aplica al jugador lesionado via
 *   equipoPartido.decorarTitular(jugador, LesionDecorator::new))
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - OCP (Open/Closed Principle): cumple porque extiende el comportamiento de cualquier
 *   IJugador sin modificar clases existentes. La lesion se modela como una capa
 *   agregada en tiempo de ejecucion.
 */
public class LesionDecorator extends JugadorDecorator {
    public LesionDecorator(IJugador jugador) {
        super(jugador);
    }

    @Override
    public double getRendimiento() {
        return jugador.getRendimiento() * 0.3;
    }

    @Override
    public String getNombreDecorador() {
        return "Lesion";
    }

    @Override
    public String getImpactoDecorador() {
        return "x0.30";
    }
}
