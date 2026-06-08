package simulador.torneo.decorator;

import simulador.torneo.domain.IJugador;

public class CansancioDecorator extends JugadorDecorator {
    private int minutoJugado;

    public CansancioDecorator(IJugador jugador, int minutoJugado) {
        super(jugador);
        this.minutoJugado = minutoJugado;
    }

    @Override
    public int getRendimiento() {
        // TODO: reducir rendimiento según minutos
        return jugador.getRendimiento();
    }
}
