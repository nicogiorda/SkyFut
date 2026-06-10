package simulador.torneo.dto;

import java.util.List;

public class ResumenTorneo {
    private final String nombreTorneo;
    private final List<ResultadoPartido> resultados;
    private final List<Goleador> goleadores;
    private final boolean completo;

    public ResumenTorneo(
            String nombreTorneo,
            List<ResultadoPartido> resultados,
            List<Goleador> goleadores,
            boolean completo) {
        this.nombreTorneo = nombreTorneo;
        this.resultados = List.copyOf(resultados);
        this.goleadores = List.copyOf(goleadores);
        this.completo = completo;
    }

    public String getNombreTorneo() {
        return nombreTorneo;
    }

    public List<ResultadoPartido> getResultados() {
        return resultados;
    }

    public List<Goleador> getGoleadores() {
        return goleadores;
    }

    public boolean isCompleto() {
        return completo;
    }
}
