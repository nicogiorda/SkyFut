package simulador.dto;

import java.util.List;

/**
 * [PATRON: (ninguno) — DTO inmutable]
 *
 * Que hace: Empaqueta el resumen completo de un torneo: nombre, lista de resultados,
 * lista de goleadores y si el torneo esta completo. Es inmutable: usa List.copyOf()
 * para copias defensivas de las listas. Es el objeto de retorno de
 * TorneoFacade.consultarResultados().
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: List<ResultadoPartido> resultados (copia defensiva inmutable),
 *   List<Goleador> goleadores (copia defensiva inmutable)
 * - Asociacion con: (ninguna adicional)
 * - Usada por (dependencia): TorneoFacade (la retorna en consultarResultados()),
 *   RepositorioPartido (la construye en consultarResumen()),
 *   SkyFutFrame (la muestra en la UI)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque la UI solo recibe este DTO con datos planos;
 *   no necesita conocer Torneo, Fase, Partido ni ningun objeto de dominio.
 */
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
