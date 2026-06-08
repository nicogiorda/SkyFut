package simulador.torneo.decorator;

import simulador.torneo.domain.IJugador;

public class LesionDecorator extends JugadorDecorator {

    public LesionDecorator(IJugador jugador) {
        super(jugador);
    }

    @Override
    public int getRendimiento() {
        // TODO: reducir rendimiento por lesión
        return jugador.getRendimiento();
    }
}
