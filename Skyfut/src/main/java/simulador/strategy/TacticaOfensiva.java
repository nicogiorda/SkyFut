package simulador.strategy;

public class TacticaOfensiva implements TacticaStrategy {
    @Override
    public double getModificadorAtaque() {
        return 1.2;
    }

    @Override
    public double getModificadorDefensa() {
        return 0.85;
    }

    @Override
    public String getFormacion() {
        return "Ofensiva";
    }
}
