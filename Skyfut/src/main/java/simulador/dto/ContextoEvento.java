package simulador.dto;

import simulador.domain.Equipo;

public class ContextoEvento {
    private int minuto;
    private Equipo local;
    private Equipo visitante;
    private boolean esSegundoTiempo;

    public ContextoEvento(int minuto, Equipo local, Equipo visitante, boolean esSegundoTiempo) {
        this.minuto = minuto;
        this.local = local;
        this.visitante = visitante;
        this.esSegundoTiempo = esSegundoTiempo;
    }

    public double getRendimientoAtaque(Equipo e ){
        return e.getRendimientoTotal() * e.getTactica().getModificadorAtaque();
    };

    public double getRendimientoDefensa(Equipo e ){
        return e.getRendimientoTotal() * e.getTactica().getModificadorDefensa();
    };

}
