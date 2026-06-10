package simulador.motor;

import java.util.List;

import simulador.composite.Partido;
import simulador.dto.ContextoEvento;
import simulador.events.CambioFactory;
import simulador.events.EventoFactory;
import simulador.events.EventoPartido;
import simulador.events.GolFactory;
import simulador.events.LesionFactory;
import simulador.events.TarjetaFactory;

public class MotorSimulacion {
    private final List<EventoFactory> eventoFactories;

    public MotorSimulacion() {
        this(List.of(
                new GolFactory(),
                new TarjetaFactory(),
                new LesionFactory(),
                new CambioFactory()));
    }

    public MotorSimulacion(List<EventoFactory> eventoFactories) {
        this.eventoFactories = List.copyOf(eventoFactories);
    }

    public void simularPartido(Partido partido) {
        partido.iniciar();

        while (!partido.estaCompleto()) {
            partido.avanzarMinuto();

            if (!partido.getEstado().permiteEventosJuego()) {
                continue;
            }

            ContextoEvento contexto = new ContextoEvento(
                    partido.getMinuto(),
                    partido.getLocal(),
                    partido.getVisitante(),
                    partido.getMinuto() > 45);

            for (EventoFactory factory : eventoFactories) {
                factory.crearEvento(contexto).ifPresent(evento -> aplicarYRegistrar(partido, evento));
            }
        }
    }

    public void simularAutomatico(Partido partido) {
        simularPartido(partido);
    }

    private void aplicarYRegistrar(Partido partido, EventoPartido evento) {
        evento.aplicar(partido);
        partido.registrarEvento(evento);
    }
}
