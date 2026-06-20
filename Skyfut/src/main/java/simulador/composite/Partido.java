package simulador.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import simulador.domain.Equipo;
import simulador.dto.Goleador;
import simulador.dto.ResultadoPartido;
import simulador.events.EventoPartido;
import simulador.events.Gol;
import simulador.state.EstadoPartido;
import simulador.state.Finalizado;
import simulador.state.NoIniciado;

/**
 * [PATRON: Composite — Leaf; State — Context (doble rol)]
 *
 * Que hace: Representa un partido individual. Como Leaf del Composite, es la hoja
 * del arbol: implementa getResultados() y getGoleadores() con sus propios datos.
 * Como Context del State, delega iniciar() y avanzarMinuto() al EstadoPartido actual,
 * que gestiona las transiciones (NoIniciado → PrimerTiempo → Entretiempo → SegundoTiempo
 * → Finalizado). En caso de empate, getGanador() desempata por rendimientoTotal.
 *
 * Relaciones:
 * - Implementa: ComponenteTorneo (rol Leaf del Composite)
 * - Composicion con: Equipo local, Equipo visitante (el partido contiene a los equipos
 *   durante la simulacion), List<EventoPartido> eventos (acumula todos los eventos)
 * - Asociacion con: EstadoPartido estado (puede cambiar en tiempo de ejecucion
 *   via setEstado(); el partido no crea los estados, los estados se autoconstruyen)
 * - Usada por (dependencia): MotorSimulacion (lo conduce minuto a minuto),
 *   GestorPartido (lo prepara y cierra), RepositorioPartido (lo persiste),
 *   TorneoFacade (lo expone a la UI)
 * - Crea (Creator GRASP): ResultadoPartido y Goleador (en getResultados/getGoleadores)
 *
 * GRASP:
 * - Information Expert: cumple porque es quien conoce su propio marcador, estado,
 *   equipos y eventos, y por lo tanto puede calcular el ganador y los goleadores.
 */
public class Partido implements ComponenteTorneo {
    private final int id;
    private Equipo local;
    private Equipo visitante;
    private int golesLocal;
    private int golesVisitante;
    private EstadoPartido estado;
    private int minuto;
    private List<EventoPartido> eventos;

    public Partido(Equipo local, Equipo visitante) {
        this(0, local, visitante);
    }

    public Partido(int id, Equipo local, Equipo visitante) {
        this.id = id;
        this.local = local;
        this.visitante = visitante;
        this.estado = new NoIniciado();
        this.eventos = new ArrayList<>();
    }

    public int getId() { return id; }
    public Equipo getLocal() { return local; }
    public Equipo getVisitante() { return visitante; }
    public int getGolesLocal() { return golesLocal; }
    public int getGolesVisitante() { return golesVisitante; }
    public EstadoPartido getEstado() { return estado; }
    public int getMinuto() { return minuto; }

    public List<EventoPartido> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    public void iniciar() {
        estado.iniciar(this);
    }

    public void avanzarMinuto() {
        minuto++;
        if (minuto == 45 || minuto == 46 || minuto == 90) {
            estado.avanzar(this);
        }
    }

    public void setEstado(EstadoPartido e) {
        this.estado = e;
    }

    public void registrarEvento(EventoPartido e) {
        eventos.add(e);
    }

    public Equipo getGanador() {
        if (!estaCompleto()) {
            return null;
        }
        if (golesLocal == golesVisitante) {
            return local.getRendimientoTotal() >= visitante.getRendimientoTotal() ? local : visitante;
        }
        return golesLocal > golesVisitante ? local : visitante;
    }

    @Override
    public List<ResultadoPartido> getResultados() {
        ResultadoPartido resultado = new ResultadoPartido(
                local.getNombre(),
                visitante.getNombre(),
                golesLocal,
                golesVisitante,
                getGanador() != null ? getGanador().getNombre() : null,
                estado.getNombre());
        return List.of(resultado);
    }

    @Override
    public boolean estaCompleto() {
        return estado instanceof Finalizado;
    }

    @Override
    public List<Goleador> getGoleadores() {
        // Acumula goles por jugador recorriendo los eventos de tipo Gol
        Map<String, int[]> golesMap = new LinkedHashMap<>();
        Map<String, String> equipoMap = new HashMap<>();

        for (EventoPartido e : eventos) {
            if (e instanceof Gol gol) {
                String nombre = gol.getAutor().getNombre();
                golesMap.merge(nombre, new int[]{1}, (acum, uno) -> { acum[0]++; return acum; });
                equipoMap.put(nombre, gol.getEquipo().getNombre());
            }
        }

        return golesMap.entrySet().stream()
                .map(entry -> new Goleador(entry.getKey(), equipoMap.get(entry.getKey()), entry.getValue()[0]))
                .collect(Collectors.toList());
    }

    public void incrementarGolesLocal() { golesLocal++; }
    public void incrementarGolesVisitante() { golesVisitante++; }
}
