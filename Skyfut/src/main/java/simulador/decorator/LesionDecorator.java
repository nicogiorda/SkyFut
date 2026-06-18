package simulador.decorator;

import simulador.domain.IJugador;

public class LesionDecorator extends JugadorDecorator {
    public LesionDecorator(IJugador jugador) {
        super(jugador);
    }

    @Override
    public double getRendimiento() {
        return jugador.getRendimiento() * 0.3;
    }

    @Override
    public String getNombreDecorador() {
        return "Lesion";
    }

    @Override
    public String getImpactoDecorador() {
        return "x0.30";
    }
}
