package simulador.events;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;

public class LesionFactory implements EventoFactory {
    private static final double PROBABILIDAD_LESION = 0.006;
    private final Random random = new Random();

    @Override
    public Optional<EventoPartido> crearEvento(ContextoEvento ctx) {
        if (random.nextDouble() >= PROBABILIDAD_LESION) {
            return Optional.empty();
        }

        Equipo equipo = random.nextBoolean() ? ctx.getLocal() : ctx.getVisitante();
        List<IJugador> titulares = equipo.getTitulares();
        if (titulares.isEmpty()) {
            return Optional.empty();
        }

        IJugador jugador = titulares.get(random.nextInt(titulares.size()));
        return Optional.of(new Lesion(ctx.getMinuto(), jugador, equipo));
    }
}
