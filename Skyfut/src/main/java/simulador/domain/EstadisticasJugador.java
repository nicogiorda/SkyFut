package simulador.domain;

/**
 * [PATRON: (ninguno) — Contenedor de datos de dominio]
 *
 * Que hace: Acumula las estadisticas de un jugador durante un partido especifico:
 * goles, asistencias, tarjetas amarillas, lesion, minutos jugados y rendimiento final.
 * Se crea una instancia por jugador por partido. No es un record porque sus campos
 * se actualizan progresivamente a medida que CalculadorEstadisticas procesa los eventos.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Asociacion con: IJugador (referencia al jugador al que pertenecen estas estadisticas;
 *   la asociacion es de solo lectura, campo final)
 * - Usada por (dependencia): CalculadorEstadisticas (la crea y popula),
 *   GestorPartido (la recibe y pasa al repositorio),
 *   RepositorioPartido (la persiste en estadistica_jugador y estado_jugador_torneo)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Alta Cohesion: cumple porque agrupa exclusivamente los datos estadisticos
 *   de un jugador en un partido, sin mezclar logica de negocio.
 * - Information Expert: cumple porque es quien sabe como incrementar sus propios
 *   contadores (incrementarGoles, incrementarTarjetas, etc.).
 */
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
