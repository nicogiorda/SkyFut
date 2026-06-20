package simulador.dto;

/**
 * [PATRON: (ninguno) — DTO inmutable]
 *
 * Que hace: Transporta el resultado de un partido (equipos, marcador, ganador, estado)
 * para su presentacion sin exponer el objeto Partido de dominio. Es inmutable:
 * todos los campos son final. Se crea desde Partido.getResultados() o desde
 * RepositorioPartido al consultar resultados desde la base de datos.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Asociacion con: (ninguna — solo tipos primitivos y Strings)
 * - Usada por (dependencia): ComponenteTorneo.getResultados() y sus implementaciones,
 *   ResumenTorneo (la contiene en su lista), RepositorioPartido (la crea),
 *   SkyFutFrame (la muestra en la UI)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque desacopla la capa de presentacion del objeto
 *   Partido de dominio; la UI nunca necesita conocer la clase Partido.
 */
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
