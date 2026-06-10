package simulador.strategy;

public class TacticaEquilibrada implements TacticaStrategy {
    @Override
    public double getModificadorAtaque() {
        return 1.0;
    }

    @Override
    public double getModificadorDefensa() {
        return 1.0;
    }

    @Override
    public String getFormacion() {
        return "Equilibrada";
    }
}
