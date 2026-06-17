package simulador.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.domain.EstadisticasJugador;
import simulador.domain.IJugador;
import simulador.dto.EquipoFixture;
import simulador.dto.FixturePartido;
import simulador.dto.ResumenTorneo;
import simulador.events.Cambio;
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
    private String nombreTorneoActual = NOMBRE_TORNEO_BASE;
    private Equipo equipoDt;
    private Partido partidoActual;

    public TorneoFacade() {
        this.repositorioTorneo = new RepositorioTorneo();
        this.repositorioEquipo = new RepositorioEquipo();
        this.repositorioPartido = new RepositorioPartido(repositorioEquipo);
        this.motor = new MotorSimulacion();
    }

    public void iniciarTorneo() {
        nombreTorneoActual = generarNombreTorneo();
        idTorneo = repositorioTorneo.crearTorneo(nombreTorneoActual);
        if (equipoDt != null) {
            repositorioTorneo.asignarEquipoDT(idTorneo, equipoDt.getId());
        }

        int idFaseOctavos = repositorioTorneo.crearFase(idTorneo, FASE_OCTAVOS, 1);

        List<EquipoFixture> equipos = repositorioTorneo.listarEquiposConPlantelCompleto(EQUIPOS_OCTAVOS);
        if (equipos.size() != EQUIPOS_OCTAVOS) {
            throw new IllegalStateException("Se necesitan exactamente 16 equipos para generar octavos");
        }

        Collections.shuffle(equipos);
        repositorioTorneo.guardarPartidosIniciales(idFaseOctavos, equipos);
    }

    public Partido prepararSiguientePartido() {
        validarTorneoIniciado();
        partidoActual = repositorioPartido.buscarSiguientePartidoNoIniciado(idTorneo)
                .orElseThrow(() -> new IllegalStateException("No hay partidos pendientes"));
        return partidoActual;
    }

    public void simularSiguientePartido() {
        Partido partido = prepararSiguientePartido();
        if (partidoEsDelDT(partido)) {
            motor.simularPrimerTiempo(partido);
            return;
        }

        motor.simularPartido(partido);
        cerrarPartido(partido, true);
    }

    public void simularPrimerTiempoPartidoActual() {
        validarPartidoActual();
        motor.simularPrimerTiempo(partidoActual);
    }

    public void simularSegundoTiempoPartidoActual() {
        validarPartidoActual();
        simularCambiosAutomaticosRivalesEntretiempo();
        motor.simularSegundoTiempo(partidoActual);
        cerrarPartido(partidoActual, true);
    }

    public void simularRestoTorneoAutomatico() {
        validarTorneoIniciado();
        while (true) {
            Partido partido = repositorioPartido.buscarSiguientePartidoNoIniciado(idTorneo).orElse(null);
            if (partido == null) {
                return;
            }
            partidoActual = partido;
            motor.simularPartido(partido);
            cerrarPartido(partido, false);
        }
    }

    public ResumenTorneo consultarResultados() {
        validarTorneoIniciado();
        return repositorioPartido.consultarResumen(idTorneo, nombreTorneoActual);
    }

    public List<FixturePartido> consultarFixture() {
        validarTorneoIniciado();
        return repositorioPartido.listarFixture(idTorneo);
    }

    public String consultarCampeon() {
        validarTorneoIniciado();
        return repositorioTorneo.buscarNombreCampeon(idTorneo).orElse(null);
    }

    public void realizarCambio(Equipo equipo, IJugador sale, IJugador entra) {
        validarPartidoActual();
        if (!partidoActual.getEstado().permiteCambios()) {
            throw new IllegalStateException("Los cambios solo estan permitidos durante el entretiempo");
        }

        Cambio cambio = new Cambio(partidoActual.getMinuto(), sale, entra, equipo);
        cambio.aplicar(partidoActual);
        partidoActual.registrarEvento(cambio);
    }

    public void cambiarTactica(Equipo equipo, TacticaStrategy tactica) {
        validarPartidoActual();
        if (!partidoActual.getEstado().permiteCambios()) {
            throw new IllegalStateException("El cambio de tactica solo esta permitido durante el entretiempo");
        }
        equipo.setTactica(tactica);
    }

    public List<Equipo> cargarEquipos() {
        return repositorioEquipo.listarEquipos();
    }

    public Equipo seleccionarEquipo(int id) {
        equipoDt = repositorioEquipo.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un equipo con id " + id));
        if (idTorneo > 0) {
            repositorioTorneo.asignarEquipoDT(idTorneo, equipoDt.getId());
        }
        return equipoDt;
    }

    public Partido getPartidoActual() {
        return partidoActual;
    }

    public Equipo getEquipoDt() {
        return equipoDt;
    }

    public boolean torneoIniciado() {
        return idTorneo > 0;
    }

    public boolean partidoActualEsDelDT() {
        return partidoActual != null && partidoEsDelDT(partidoActual);
    }

    public boolean partidoActualEstaEnEntretiempo() {
        return partidoActual != null && partidoActual.getEstado().permiteCambios();
    }

    private void simularCambiosAutomaticosRivalesEntretiempo() {
        if (!partidoActual.getEstado().permiteCambios()) {
            return;
        }

        if (!partidoActualEsDelDT()) {
            motor.simularCambiosAutomaticosEntretiempo(partidoActual);
            return;
        }

        if (equipoDt == null) {
            return;
        }

        Equipo rival = partidoActual.getLocal().getId() == equipoDt.getId()
                ? partidoActual.getVisitante()
                : partidoActual.getLocal();
        motor.simularCambioAutomaticoEntretiempo(partidoActual, rival);
    }

    private void cerrarPartido(Partido partido, boolean simularRestoSiDtEliminado) {
        List<EstadisticasJugador> stats = calcularEstadisticas(partido);
        repositorioPartido.guardarResultado(partido);
        repositorioPartido.guardarEventos(partido);
        repositorioPartido.guardarEstadisticas(partido.getId(), stats);
        for (EstadisticasJugador stat : stats) {
            repositorioPartido.actualizarEstadoJugadorTorneo(idTorneo, stat);
        }

        avanzarFaseSiCorresponde(partido);

        if (simularRestoSiDtEliminado && equipoDt != null && partidoEsDelDT(partido) && !equipoDtGano(partido)) {
            simularRestoTorneoAutomatico();
        }
    }

    private void avanzarFaseSiCorresponde(Partido partido) {
        RepositorioTorneo.FaseInfo fase = repositorioTorneo.buscarFaseDePartido(partido.getId())
                .orElseThrow(() -> new IllegalStateException("No se encontro la fase del partido"));

        if (!repositorioTorneo.faseCompleta(fase.id())) {
            return;
        }

        repositorioTorneo.marcarFaseCompleta(fase.id());
        List<EquipoFixture> ganadores = repositorioTorneo.listarGanadoresFase(fase.id());

        if (ganadores.size() == 1) {
            repositorioTorneo.finalizarTorneo(idTorneo, ganadores.get(0).idEquipo());
            repositorioPartido.limpiarEstadisticasTorneo(idTorneo);
            return;
        }

        int ordenSiguiente = fase.orden() + 1;
        if (repositorioTorneo.faseExiste(idTorneo, ordenSiguiente)) {
            return;
        }

        int idFaseSiguiente = repositorioTorneo.crearFase(
                idTorneo,
                nombreFaseSiguiente(ganadores.size()),
                ordenSiguiente);
        repositorioTorneo.guardarPartidosIniciales(idFaseSiguiente, ganadores);
    }

    private String nombreFaseSiguiente(int cantidadEquipos) {
        return switch (cantidadEquipos) {
            case 8 -> "Cuartos de Final";
            case 4 -> "Semifinal";
            case 2 -> "Final";
            default -> "Fase " + cantidadEquipos + " equipos";
        };
    }

    private List<EstadisticasJugador> calcularEstadisticas(Partido partido) {
        Map<Integer, EstadisticasJugador> statsMap = new LinkedHashMap<>();
        registrarPlantelCompleto(statsMap, partido.getLocal(), partido.getMinuto());
        registrarPlantelCompleto(statsMap, partido.getVisitante(), partido.getMinuto());

        for (EventoPartido e : partido.getEventos()) {
            if (e instanceof Gol gol) {
                EstadisticasJugador s = obtenerStats(statsMap, gol.getAutor(), gol.getEquipo(), partido.getMinuto());
                s.incrementarGoles();
            } else if (e instanceof Tarjeta t) {
                EstadisticasJugador s = obtenerStats(statsMap, t.getJugador(), t.getEquipo(), partido.getMinuto());
                s.incrementarTarjetas();
            } else if (e instanceof Lesion l) {
                EstadisticasJugador s = obtenerStats(statsMap, l.getJugador(), l.getEquipo(), partido.getMinuto());
                s.setLesionado(true);
            } else if (e instanceof Cambio c) {
                obtenerStats(statsMap, c.getSale(), c.getEquipo(), c.getMinuto()).setMinutosJugados(c.getMinuto());
                obtenerStats(statsMap, c.getEntra(), c.getEquipo(), partido.getMinuto());
            }
        }

        return new ArrayList<>(statsMap.values());
    }

    private void registrarPlantelCompleto(Map<Integer, EstadisticasJugador> statsMap, Equipo equipo, int minutos) {
        for (IJugador jugador : equipo.getTitulares()) {
            obtenerStats(statsMap, jugador, equipo, minutos);
        }
        for (IJugador jugador : equipo.getSuplentes()) {
            obtenerStats(statsMap, jugador, equipo, 0);
        }
    }

    private EstadisticasJugador obtenerStats(
            Map<Integer, EstadisticasJugador> statsMap,
            IJugador jugador,
            Equipo equipo,
            int minutosJugados) {
        EstadisticasJugador stats = statsMap.computeIfAbsent(jugador.getId(), id -> new EstadisticasJugador(jugador, equipo.getId()));
        stats.setMinutosJugados(Math.max(stats.getMinutosJugados(), minutosJugados));
        stats.setRendimientoFinal(jugador.getRendimiento());
        return stats;
    }

    private boolean partidoEsDelDT(Partido partido) {
        if (equipoDt == null) {
            return false;
        }
        return partido.getLocal().getId() == equipoDt.getId()
                || partido.getVisitante().getId() == equipoDt.getId();
    }

    private boolean equipoDtGano(Partido partido) {
        return partido.getGanador() != null && partido.getGanador().getId() == equipoDt.getId();
    }

    private void validarTorneoIniciado() {
        if (idTorneo < 0) {
            throw new IllegalStateException("El torneo no ha sido iniciado");
        }
    }

    private void validarPartidoActual() {
        if (partidoActual == null) {
            throw new IllegalStateException("No hay un partido actual");
        }
    }

    private String generarNombreTorneo() {
        return NOMBRE_TORNEO_BASE + " - " + UUID.randomUUID().toString().substring(0, 8);
    }
}
