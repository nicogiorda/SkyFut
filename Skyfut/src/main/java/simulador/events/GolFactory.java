package simulador.events;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;

/**
 * [PATRON: Factory Method — ConcreteCreator]
 *
 * Que hace: Decide si ocurre un gol en un minuto dado y, en caso afirmativo,
 * crea el objeto Gol correspondiente. La probabilidad es dinamica:
 * min(0.12, max(0.01, ataque/(ataque+defensa) * 0.08)), donde ataque y defensa
 * se obtienen de ContextoEvento aplicando los modificadores tacticos de cada equipo.
 * Elige aleatoriamente cual de los dos equipos ataca y cual de sus titulares anota.
 *
 * Relaciones:
 * - Implementa: EventoFactory
 * - Composicion con: Random random (instancia propia para aleatoriedad)
 * - Asociacion con: (ninguna persistente)
 * - Usada por (dependencia): ContextoEvento (consulta rendimientos),
 *   Equipo (obtiene titulares), MotorSimulacion (la tiene en su lista de factories)
 * - Crea (Creator GRASP): Gol
 *
 * GRASP:
 * - Creator (GRASP Creator): cumple porque tiene toda la informacion necesaria
 *   para crear un Gol: el minuto, el autor y el equipo, derivados del ContextoEvento.
 */
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
        return Optional.of(new Gol(ctx.getMinuto(), autor, equipoAtacante));
    }
}
