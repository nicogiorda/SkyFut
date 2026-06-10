package simulador.decorator;

import simulador.domain.IJugador;

public abstract class JugadorDecorator implements IJugador {
    protected IJugador jugador;

    protected JugadorDecorator(IJugador jugador) {
        this.jugador = jugador;
    }

    @Override
    public String getNombre() {
        return jugador.getNombre();
    }

    @Override
    public String getPosicion() {
        return jugador.getPosicion();
    }

    @Override
    public double getRendimiento() {
        return jugador.getRendimiento();
    }
}
