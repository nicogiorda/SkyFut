package simulador.decorator;

import simulador.domain.IJugador;

/**
 * [PATRON: Decorator — ConcreteDecorator]
 *
 * Que hace: Aumenta el rendimiento de un jugador en un 20% (multiplicador 1.20)
 * para representar el boost de confianza tras anotar un gol. Se aplica al autor
 * del gol mediante Equipo.decorarTitular() cuando se procesa un evento Gol.
 *
 * Relaciones:
 * - Hereda de: JugadorDecorator
 * - Composicion con: (ninguna adicional a la del padre)
 * - Asociacion con: (ninguna)
 * - Usada por (dependencia): Gol.aplicar() (la aplica al autor del gol via
 *   equipoPartido.decorarTitular(autor, GolDecorator::new))
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - OCP (Open/Closed Principle): cumple porque agrega el efecto de gol al jugador
 *   sin modificar la clase Jugador. Cualquier jugador puede recibir este bonus
 *   en tiempo de ejecucion mediante composicion.
 */
public class GolDecorator extends JugadorDecorator {
    private static final double MODIFICADOR_GOL = 1.20;

    public GolDecorator(IJugador jugador) {
        super(jugador);
    }

    @Override
    public double getRendimiento() {
        return jugador.getRendimiento() * MODIFICADOR_GOL;
    }

    @Override
    public String getNombreDecorador() {
        return "Gol";
    }

    @Override
    public String getImpactoDecorador() {
        return "x1.20";
    }
}
