package simulador.torneo.decorator;

import simulador.torneo.domain.IJugador;

public abstract class JugadorDecorator implements IJugador {
    protected IJugador jugador;

    public JugadorDecorator(IJugador jugador) { this.jugador = jugador; }

    @Override
    public String getNombre() { return jugador.getNombre(); }

    @Override
    public int getRendimiento() { return jugador.getRendimiento(); }
}
