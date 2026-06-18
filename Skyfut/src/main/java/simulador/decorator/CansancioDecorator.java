package simulador.decorator;

import simulador.domain.IJugador;

public class CansancioDecorator extends JugadorDecorator {
    private int minutoJugado;

    public CansancioDecorator(IJugador jugador, int minutoJugado) {
        super(jugador);
        this.minutoJugado = minutoJugado;
    }

    @Override
    public double getRendimiento() {
        return jugador.getRendimiento() * getModificador();
    }

    @Override
    public String getNombreDecorador() {
        return "Cansancio";
    }

    @Override
    public String getImpactoDecorador() {
        return "x" + String.format("%.2f", getModificador());
    }

    private double getModificador() {
        return Math.max(0.5, 1.0 - (minutoJugado / 300.0));
    }
}
