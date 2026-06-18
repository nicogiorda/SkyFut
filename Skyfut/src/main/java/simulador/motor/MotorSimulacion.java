package simulador.motor;

import java.util.List;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.dto.ContextoEvento;
import simulador.events.CambioFactory;
import simulador.events.EventoFactory;
import simulador.events.EventoPartido;
import simulador.events.GolFactory;
import simulador.events.LesionFactory;
import simulador.events.TarjetaFactory;

public class MotorSimulacion {
    private static final double MODIFICADOR_EQUIPO_DT = 1.12;

    private final List<EventoFactory> eventoFactories;
    private final CambioFactory cambioFactory;

    public MotorSimulacion() {
        this(List.of(
                new GolFactory(),
                new TarjetaFactory(),
                new LesionFactory()));
    }

    public MotorSimulacion(List<EventoFactory> eventoFactories) {
        this.eventoFactories = List.copyOf(eventoFactories);
        this.cambioFactory = new CambioFactory();
    }

    public void simularPartido(Partido partido) {
        simularPrimerTiempo(partido);
        simularCambiosAutomaticosEntretiempo(partido);
        simularSegundoTiempo(partido);
    }

    public void simularPrimerTiempo(Partido partido) {
        simularPrimerTiempo(partido, null);
    }

    public void simularPrimerTiempo(Partido partido, Equipo equipoFavorecido) {
        if (partido.getMinuto() == 0) {
            partido.iniciar();
        }

        while (!partido.getEstado().permiteCambios() && !partido.estaCompleto()) {
            simularMinuto(partido, equipoFavorecido);
        }
    }

    public void simularSegundoTiempo(Partido partido) {
        simularSegundoTiempo(partido, null);
    }

    public void simularSegundoTiempo(Partido partido, Equipo equipoFavorecido) {
        if (!partido.getEstado().permiteCambios()) {
            throw new IllegalStateException("El segundo tiempo solo puede iniciarse desde el entretiempo");
        }

        while (!partido.estaCompleto()) {
            simularMinuto(partido, equipoFavorecido);
        }
    }

    public void simularCambiosAutomaticosEntretiempo(Partido partido) {
        if (!partido.getEstado().permiteCambios()) {
            throw new IllegalStateException("Los cambios automaticos solo pueden ejecutarse en entretiempo");
        }

        simularCambioAutomaticoEntretiempo(partido, partido.getLocal());
        simularCambioAutomaticoEntretiempo(partido, partido.getVisitante());
    }

    public void simularCambioAutomaticoEntretiempo(Partido partido, simulador.domain.Equipo equipo) {
        if (!partido.getEstado().permiteCambios()) {
            throw new IllegalStateException("Los cambios automaticos solo pueden ejecutarse en entretiempo");
        }

        cambioFactory.crearCambioAutomatico(partido.getMinuto(), equipo)
                .ifPresent(evento -> aplicarYRegistrar(partido, evento));
    }

    public void simularAutomatico(Partido partido) {
        simularPartido(partido);
    }

    private void simularMinuto(Partido partido) {
        simularMinuto(partido, null);
    }

    private void simularMinuto(Partido partido, Equipo equipoFavorecido) {
        partido.avanzarMinuto();

        if (!partido.getEstado().permiteEventosJuego()) {
            return;
        }

        int idEquipoFavorecido = equipoFavorecido == null ? -1 : equipoFavorecido.getId();
        ContextoEvento contexto = new ContextoEvento(
                partido.getMinuto(),
                partido.getLocal(),
                partido.getVisitante(),
                partido.getMinuto() > 45,
                idEquipoFavorecido,
                MODIFICADOR_EQUIPO_DT);

        for (EventoFactory factory : eventoFactories) {
            factory.crearEvento(contexto).ifPresent(evento -> aplicarYRegistrar(partido, evento));
        }
    }

    private void aplicarYRegistrar(Partido partido, EventoPartido evento) {
        evento.aplicar(partido);
        partido.registrarEvento(evento);
    }
}
