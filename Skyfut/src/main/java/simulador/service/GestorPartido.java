package simulador.service;

import java.util.List;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.domain.EstadisticasJugador;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;
import simulador.events.CambioFactory;
import simulador.events.EventoPartido;
import simulador.motor.MotorSimulacion;
import simulador.repositorio.RepositorioPartido;
import simulador.strategy.TacticaStrategy;

public class GestorPartido {
    private final RepositorioPartido repositorioPartido;
    private final MotorSimulacion motor;
    private final CalculadorEstadisticas calculadorEstadisticas;
    private final GestorFases gestorFases;

    public GestorPartido(
            RepositorioPartido repositorioPartido,
            MotorSimulacion motor,
            CalculadorEstadisticas calculadorEstadisticas,
            GestorFases gestorFases) {
        this.repositorioPartido = repositorioPartido;
        this.motor = motor;
        this.calculadorEstadisticas = calculadorEstadisticas;
        this.gestorFases = gestorFases;
    }

    public Partido prepararSiguientePartido(int idTorneo) {
        return repositorioPartido.buscarSiguientePartidoNoIniciado(idTorneo)
                .orElseThrow(() -> new IllegalStateException("No hay partidos pendientes"));
    }

    public void simularSiguientePartido(int idTorneo, Equipo equipoDt, Partido partido) {
        if (partidoEsDelDT(partido, equipoDt)) {
            motor.simularPrimerTiempo(partido);
            return;
        }

        motor.simularPartido(partido);
        cerrarPartido(idTorneo, partido, equipoDt, true);
    }

    public void simularPrimerTiempo(Partido partido) {
        motor.simularPrimerTiempo(partido);
    }

    public void simularSegundoTiempo(int idTorneo, Partido partido, Equipo equipoDt) {
        simularCambiosAutomaticosRivalesEntretiempo(partido, equipoDt);
        motor.simularSegundoTiempo(partido);
        cerrarPartido(idTorneo, partido, equipoDt, true);
    }

    public void simularRestoTorneoAutomatico(int idTorneo, Equipo equipoDt) {
        while (true) {
            Partido partido = repositorioPartido.buscarSiguientePartidoNoIniciado(idTorneo).orElse(null);
            if (partido == null) {
                return;
            }
            motor.simularPartido(partido);
            cerrarPartido(idTorneo, partido, equipoDt, false);
        }
    }

    public void realizarCambio(Partido partido, Equipo equipo, IJugador sale, IJugador entra) {
        if (!partido.getEstado().permiteCambios()) {
            throw new IllegalStateException("Los cambios solo estan permitidos durante el entretiempo");
        }

        ContextoEvento contexto = new ContextoEvento(
                partido.getMinuto(),
                partido.getLocal(),
                partido.getVisitante(),
                partido.getMinuto() > 45);

        EventoPartido cambio = new CambioFactory(sale, entra, equipo)
                .crearEvento(contexto)
                .orElseThrow(() -> new IllegalStateException("No se pudo crear el evento de cambio"));
        cambio.aplicar(partido);
        partido.registrarEvento(cambio);
    }

    public void cambiarTactica(Partido partido, Equipo equipo, TacticaStrategy tactica) {
        if (!partido.getEstado().permiteCambios()) {
            throw new IllegalStateException("El cambio de tactica solo esta permitido durante el entretiempo");
        }
        equipo.setTactica(tactica);
    }

    public boolean partidoEsDelDT(Partido partido, Equipo equipoDt) {
        if (partido == null || equipoDt == null) {
            return false;
        }
        return partido.getLocal().getId() == equipoDt.getId()
                || partido.getVisitante().getId() == equipoDt.getId();
    }

    private void simularCambiosAutomaticosRivalesEntretiempo(Partido partido, Equipo equipoDt) {
        if (!partido.getEstado().permiteCambios()) {
            return;
        }

        if (!partidoEsDelDT(partido, equipoDt)) {
            motor.simularCambiosAutomaticosEntretiempo(partido);
            return;
        }

        Equipo rival = partido.getLocal().getId() == equipoDt.getId()
                ? partido.getVisitante()
                : partido.getLocal();
        motor.simularCambioAutomaticoEntretiempo(partido, rival);
    }

    private void cerrarPartido(int idTorneo, Partido partido, Equipo equipoDt, boolean simularRestoSiDtEliminado) {
        List<EstadisticasJugador> stats = calculadorEstadisticas.calcular(partido);
        repositorioPartido.guardarResultado(partido);
        repositorioPartido.guardarEventos(partido);
        repositorioPartido.guardarEstadisticas(partido.getId(), stats);
        for (EstadisticasJugador stat : stats) {
            repositorioPartido.actualizarEstadoJugadorTorneo(idTorneo, stat);
        }

        gestorFases.avanzarFaseSiCorresponde(idTorneo, partido);

        if (simularRestoSiDtEliminado && partidoEsDelDT(partido, equipoDt) && !equipoDtGano(partido, equipoDt)) {
            simularRestoTorneoAutomatico(idTorneo, equipoDt);
        }
    }

    private boolean equipoDtGano(Partido partido, Equipo equipoDt) {
        return equipoDt != null
                && partido.getGanador() != null
                && partido.getGanador().getId() == equipoDt.getId();
    }
}
