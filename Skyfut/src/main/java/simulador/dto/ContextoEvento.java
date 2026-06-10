package simulador.dto;

import simulador.domain.Equipo;

public final class ContextoEvento {
    private final int minuto;
    private final Equipo local;
    private final Equipo visitante;
    private final boolean esSegundoTiempo;

    public ContextoEvento(int minuto, Equipo local, Equipo visitante, boolean esSegundoTiempo) {
        this.minuto = minuto;
        this.local = local;
        this.visitante = visitante;
        this.esSegundoTiempo = esSegundoTiempo;
    }

    public int getMinuto() {
        return minuto;
    }

    public Equipo getLocal() {
        return local;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public boolean isEsSegundoTiempo() {
        return esSegundoTiempo;
    }

    public double getRendimientoAtaque(Equipo e) {
        return e.getRendimientoTotal() * e.getTactica().getModificadorAtaque();
    }

    public double getRendimientoDefensa(Equipo e) {
        return e.getRendimientoTotal() * e.getTactica().getModificadorDefensa();
    }
}
