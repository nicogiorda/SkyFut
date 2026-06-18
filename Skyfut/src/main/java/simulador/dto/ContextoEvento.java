package simulador.dto;

import simulador.domain.Equipo;

public final class ContextoEvento {
    private final int minuto;
    private final Equipo local;
    private final Equipo visitante;
    private final boolean esSegundoTiempo;
    private final int idEquipoFavorecido;
    private final double modificadorEquipoFavorecido;

    public ContextoEvento(int minuto, Equipo local, Equipo visitante, boolean esSegundoTiempo) {
        this(minuto, local, visitante, esSegundoTiempo, -1, 1.0);
    }

    public ContextoEvento(
            int minuto,
            Equipo local,
            Equipo visitante,
            boolean esSegundoTiempo,
            int idEquipoFavorecido,
            double modificadorEquipoFavorecido) {
        this.minuto = minuto;
        this.local = local;
        this.visitante = visitante;
        this.esSegundoTiempo = esSegundoTiempo;
        this.idEquipoFavorecido = idEquipoFavorecido;
        this.modificadorEquipoFavorecido = modificadorEquipoFavorecido;
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
        return e.getRendimientoTotal() * e.getTactica().getModificadorAtaque() * getModificador(e);
    }

    public double getRendimientoDefensa(Equipo e) {
        return e.getRendimientoTotal() * e.getTactica().getModificadorDefensa() * getModificador(e);
    }

    private double getModificador(Equipo equipo) {
        return equipo.getId() == idEquipoFavorecido ? modificadorEquipoFavorecido : 1.0;
    }
}
