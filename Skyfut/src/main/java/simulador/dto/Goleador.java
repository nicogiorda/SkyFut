package simulador.dto;

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
