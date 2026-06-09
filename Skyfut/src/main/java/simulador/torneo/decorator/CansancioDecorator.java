package simulador.torneo.decorator;

import simulador.torneo.domain.IJugador;

public class CansancioDecorator extends JugadorDecorator {
    private int minutoJugado;

    public CansancioDecorator(IJugador jugador, int minutoJugado) {
        super(jugador);
        this.minutoJugado = minutoJugado;
    }

    @Override
    public double getRendimiento() {
        double modificador = Math.max(0.5, 1.0 - (minutoJugado / 300.0));
        return jugador.getRendimiento() * modificador;
    }
}
