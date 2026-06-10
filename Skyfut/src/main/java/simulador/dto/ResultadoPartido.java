package simulador.dto;

public class ResultadoPartido {
    private final String equipoLocal;
    private final String equipoVisitante;
    private final int golesLocal;
    private final int golesVisitante;
    private final String ganador;
    private final String estado;

    public ResultadoPartido(
            String equipoLocal,
            String equipoVisitante,
            int golesLocal,
            int golesVisitante,
            String ganador,
            String estado) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.ganador = ganador;
        this.estado = estado;
    }

    public String getEquipoLocal() {
        return equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public String getGanador() {
        return ganador;
    }

    public String getEstado() {
        return estado;
    }
}
