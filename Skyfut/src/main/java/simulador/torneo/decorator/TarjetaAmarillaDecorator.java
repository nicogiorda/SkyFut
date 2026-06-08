package simulador.torneo.decorator;

import simulador.torneo.domain.IJugador;

public class TarjetaAmarillaDecorator extends JugadorDecorator {

    public TarjetaAmarillaDecorator(IJugador jugador) {
        super(jugador);
    }

    @Override
    public int getRendimiento() {
        // TODO: reducir rendimiento por tarjeta amarilla
        return jugador.getRendimiento();
    }
}
