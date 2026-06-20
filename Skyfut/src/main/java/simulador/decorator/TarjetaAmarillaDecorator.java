package simulador.decorator;

import simulador.domain.IJugador;

/**
 * [PATRON: Decorator — ConcreteDecorator]
 *
 * Que hace: Reduce levemente el rendimiento de un jugador amonestado restando 0.10
 * a su rendimiento actual (con minimo de 0.0). Representa la inhibicion psicologica
 * y la precaucion del jugador tras recibir una tarjeta amarilla. Se aplica cuando
 * ocurre un evento Tarjeta.
 *
 * Relaciones:
 * - Hereda de: JugadorDecorator
 * - Composicion con: (ninguna adicional a la del padre)
 * - Asociacion con: (ninguna)
 * - Usada por (dependencia): Tarjeta.aplicar() (la aplica al jugador amonestado via
 *   eq.decorarTitular(jugador, j -> new TarjetaAmarillaDecorator(j)))
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - OCP (Open/Closed Principle): cumple porque agrega el efecto de amonestacion
 *   sin modificar ninguna clase existente. Los decorators se apilan de forma
 *   transparente sobre cualquier IJugador.
 */
public class TarjetaAmarillaDecorator extends JugadorDecorator {
    public TarjetaAmarillaDecorator(IJugador jugador) {
        super(jugador);
    }

    @Override
    public double getRendimiento() {
        return Math.max(0.0, jugador.getRendimiento() - 0.1);
    }

    @Override
    public String getNombreDecorador() {
        return "Amarilla";
    }

    @Override
    public String getImpactoDecorador() {
        return "-0.10";
    }
}
