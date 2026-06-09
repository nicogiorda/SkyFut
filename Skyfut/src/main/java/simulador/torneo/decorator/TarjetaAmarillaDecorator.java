package simulador.torneo.decorator;

import simulador.torneo.domain.IJugador;

public class TarjetaAmarillaDecorator extends JugadorDecorator {
    public TarjetaAmarillaDecorator(IJugador jugador) {
        super(jugador);
    }

    @Override
    public double getRendimiento() {
        return Math.max(0.0, jugador.getRendimiento() - 0.1);
    }
}
