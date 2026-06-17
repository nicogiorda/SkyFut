package simulador.domain;

public class EstadisticasJugador {
    private final IJugador jugador;
    private final int idEquipo;
    private int goles;
    private int asistencias;
    private int tarjetas;
    private int minutosJugados;
    private boolean lesionado;
    private double rendimientoFinal;

    public EstadisticasJugador(IJugador jugador, int idEquipo) {
        this.jugador = jugador;
        this.idEquipo = idEquipo;
    }

    public void incrementarGoles() { goles++; }
    public void incrementarAsistencias() { asistencias++; }
    public void incrementarTarjetas() { tarjetas++; }

    public IJugador getJugador() { return jugador; }
    public int getIdEquipo() { return idEquipo; }
    public int getGoles() { return goles; }
    public int getAsistencias() { return asistencias; }
    public int getTarjetas() { return tarjetas; }
    public int getMinutosJugados() { return minutosJugados; }
    public boolean isLesionado() { return lesionado; }
    public double getRendimientoFinal() { return rendimientoFinal; }

    public void setMinutosJugados(int minutos) { this.minutosJugados = minutos; }
    public void setLesionado(boolean lesionado) { this.lesionado = lesionado; }
    public void setRendimientoFinal(double rendimiento) { this.rendimientoFinal = rendimiento; }
}
