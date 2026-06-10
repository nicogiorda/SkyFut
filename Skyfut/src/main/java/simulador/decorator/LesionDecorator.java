package simulador.torneo.decorator;

import simulador.torneo.domain.IJugador;

public class LesionDecorator extends JugadorDecorator {
    public LesionDecorator(IJugador jugador) {
        super(jugador);
    }

    @Override
    public double getRendimiento() {
        return jugador.getRendimiento() * 0.3;
    }
}
