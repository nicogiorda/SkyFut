package simulador.torneo.strategy;

public class TacticaDefensiva implements TacticaStrategy {
    @Override
    public double getModificadorAtaque() {
        return 0.85;
    }

    @Override
    public double getModificadorDefensa() {
        return 1.2;
    }

    @Override
    public String getFormacion() {
        return "Defensiva";
    }
}
