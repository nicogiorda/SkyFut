package simulador.events;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;

public class TarjetaFactory implements EventoFactory {
    private static final double PROB_BASE = 0.02; // 2% por minuto

    @Override
    public Optional<EventoPartido> crearEvento(ContextoEvento ctx) {
        if (Math.random() > PROB_BASE) return Optional.empty();

        // la tarjeta puede ser para cualquier equipo
        Equipo equipo = Math.random() < 0.5 ? ctx.getLocal() : ctx.getVisitante();
        IJugador jugador = elegirJugador(equipo);

        return Optional.of(new Tarjeta(ctx.getMinuto(), jugador, equipo));
    }

    private IJugador elegirJugador(Equipo equipo) {
        List<IJugador> titulares = equipo.getTitulares();
        return titulares.get((int)(Math.random() * titulares.size()));
    }
}