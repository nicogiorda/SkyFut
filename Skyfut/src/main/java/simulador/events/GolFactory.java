package simulador.events;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;

public class GolFactory implements EventoFactory {
    private final Random random = new Random();

    @Override
    public Optional<EventoPartido> crearEvento(ContextoEvento ctx) {
        Equipo equipoAtacante = random.nextBoolean() ? ctx.getLocal() : ctx.getVisitante();
        Equipo equipoDefensor = equipoAtacante == ctx.getLocal() ? ctx.getVisitante() : ctx.getLocal();
        double ataque = ctx.getRendimientoAtaque(equipoAtacante);
        double defensa = ctx.getRendimientoDefensa(equipoDefensor);
        double probabilidadGol = Math.min(0.12, Math.max(0.01, ataque / (ataque + defensa) * 0.08));

        if (random.nextDouble() >= probabilidadGol) {
            return Optional.empty();
        }

        List<IJugador> titulares = equipoAtacante.getTitulares();
        if (titulares.isEmpty()) {
            return Optional.empty();
        }

        IJugador autor = titulares.get(random.nextInt(titulares.size()));
        return Optional.of(new Gol(ctx.getMinuto(), autor));
    }
}
