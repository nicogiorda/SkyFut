package simulador.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.domain.EstadisticasJugador;
import simulador.domain.IJugador;
import simulador.dto.EquipoFixture;
import simulador.dto.ResumenTorneo;
import simulador.events.EventoPartido;
import simulador.events.Gol;
import simulador.events.Lesion;
import simulador.events.Tarjeta;
import simulador.motor.MotorSimulacion;
import simulador.repositorio.RepositorioEquipo;
import simulador.repositorio.RepositorioPartido;
import simulador.repositorio.RepositorioTorneo;
import simulador.strategy.TacticaStrategy;

public class TorneoFacade {
    private static final String NOMBRE_TORNEO_BASE = "SkyFut Champions 2026";
    private static final String FASE_OCTAVOS = "Octavos de Final";
    private static final int EQUIPOS_OCTAVOS = 16;

    private final RepositorioTorneo repositorioTorneo;
    private final RepositorioEquipo repositorioEquipo;
    private final RepositorioPartido repositorioPartido;
    private final MotorSimulacion motor;

    private int idTorneo = -1;
    private Partido partidoActual = null;

    public TorneoFacade() {
        this.repositorioTorneo = new RepositorioTorneo();
        this.repositorioEquipo = new RepositorioEquipo();
        this.repositorioPartido = new RepositorioPartido(repositorioEquipo);
        this.motor = new MotorSimulacion();
    }

    public void iniciarTorneo() {
        idTorneo = repositorioTorneo.crearTorneo(generarNombreTorneo());
        int idFaseOctavos = repositorioTorneo.crearFase(idTorneo, FASE_OCTAVOS, 1);

        List<EquipoFixture> equipos = repositorioTorneo.listarEquiposConPlantelCompleto(EQUIPOS_OCTAVOS);
        if (equipos.size() != EQUIPOS_OCTAVOS) {
            throw new IllegalStateException("Se necesitan exactamente 16 equipos para generar octavos");
        }

        Collections.shuffle(equipos);
        repositorioTorneo.guardarPartidosIniciales(idFaseOctavos, equipos);
    }

    public void simularPartido(Partido partido) {
        motor.simularPartido(partido);
    }

    public void simularSiguientePartido() {
        Partido partido = repositorioPartido.buscarSiguientePartidoNoIniciado()
                .orElseThrow(() -> new IllegalStateException("No hay partidos pendientes"));

        partidoActual = partido;
        motor.simularPartido(partido);

        List<EstadisticasJugador> stats = calcularEstadisticas(partido);
        repositorioPartido.guardarResultado(partido);
        repositorioPartido.guardarEventos(partido);
        repositorioPartido.guardarEstadisticas(partido.getId(), stats);
    }

    public ResumenTorneo consultarResultados() {
        if (idTorneo < 0) {
            throw new IllegalStateException("El torneo no ha sido iniciado");
        }
        return repositorioPartido.consultarResumen(idTorneo, NOMBRE_TORNEO_BASE);
    }

    // Solo permitido durante Entretiempo (validado por State)
    public void realizarCambio(Equipo equipo, IJugador sale, IJugador entra) {
        if (partidoActual == null || !partidoActual.getEstado().permiteCambios()) {
            throw new IllegalStateException("Los cambios solo están permitidos durante el entretiempo");
        }
        equipo.sustituir(sale, entra);
    }

    // Solo permitido durante Entretiempo (validado por State)
    public void cambiarTactica(Equipo equipo, TacticaStrategy tactica) {
        if (partidoActual == null || !partidoActual.getEstado().permiteCambios()) {
            throw new IllegalStateException("El cambio de táctica solo está permitido durante el entretiempo");
        }
        equipo.setTactica(tactica);
    }

    public List<Equipo> cargarEquipos() {
        return repositorioEquipo.listarEquipos();
    }

    public Equipo seleccionarEquipo(int id) {
        return repositorioEquipo.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un equipo con id " + id));
    }

    private List<EstadisticasJugador> calcularEstadisticas(Partido partido) {
        Map<String, EstadisticasJugador> statsMap = new LinkedHashMap<>();

        // Inicializa stats para todos los titulares de ambos equipos al final del partido
        for (IJugador j : partido.getLocal().getTitulares()) {
            EstadisticasJugador s = new EstadisticasJugador(j, partido.getLocal().getId());
            s.setMinutosJugados(partido.getMinuto());
            s.setRendimientoFinal(j.getRendimiento());
            statsMap.put(j.getNombre(), s);
        }
        for (IJugador j : partido.getVisitante().getTitulares()) {
            EstadisticasJugador s = new EstadisticasJugador(j, partido.getVisitante().getId());
            s.setMinutosJugados(partido.getMinuto());
            s.setRendimientoFinal(j.getRendimiento());
            statsMap.put(j.getNombre(), s);
        }

        // Acumula goles, tarjetas y lesiones desde los eventos registrados
        for (EventoPartido e : partido.getEventos()) {
            if (e instanceof Gol gol) {
                EstadisticasJugador s = statsMap.get(gol.getAutor().getNombre());
                if (s != null) s.incrementarGoles();
            } else if (e instanceof Tarjeta t) {
                EstadisticasJugador s = statsMap.get(t.getJugador().getNombre());
                if (s != null) s.incrementarTarjetas();
            } else if (e instanceof Lesion l) {
                EstadisticasJugador s = statsMap.get(l.getJugador().getNombre());
                if (s != null) s.setLesionado(true);
            }
        }

        return new ArrayList<>(statsMap.values());
    }

    private String generarNombreTorneo() {
        return NOMBRE_TORNEO_BASE + " - " + UUID.randomUUID().toString().substring(0, 8);
    }
}
