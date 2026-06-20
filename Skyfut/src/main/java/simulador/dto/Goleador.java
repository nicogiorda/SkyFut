package simulador.dto;

/**
 * [PATRON: (ninguno) — DTO inmutable]
 *
 * Que hace: Transporta los datos de un goleador (nombre del jugador, nombre del equipo,
 * cantidad de goles) para su presentacion en resultados y resumenes. Es inmutable:
 * todos los campos son final y no tiene setters. Se crea a partir de los eventos
 * de tipo Gol acumulados en un Partido o consultados desde la base de datos.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Asociacion con: (ninguna — solo datos primitivos y Strings)
 * - Usada por (dependencia): ComponenteTorneo.getGoleadores() y sus implementaciones
 *   (Partido, Fase, Torneo), ResumenTorneo (la contiene en su lista),
 *   RepositorioPartido (la crea al consultar goleadores desde BD), SkyFutFrame (la muestra)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque no depende de ninguna clase de dominio;
 *   solo usa tipos primitivos y String, minimizando dependencias entre capas.
 */
public class Goleador {
    private final String nombreJugador;
    private final String nombreEquipo;
    private final int goles;

    public Goleador(String nombreJugador, String nombreEquipo, int goles) {
        this.nombreJugador = nombreJugador;
        this.nombreEquipo = nombreEquipo;
        this.goles = goles;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public int getGoles() {
        return goles;
    }
}
