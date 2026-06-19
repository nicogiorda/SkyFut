package simulador.decorator;

import simulador.domain.IJugador;

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
