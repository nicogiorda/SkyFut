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
 * Que hace: Decide si ocurre una tarjeta amarilla en un minuto dado con una
 * probabilidad fija del 2% (PROB_BASE = 0.02). Si ocurre, elige aleatoriamente
 * uno de los dos equipos y uno de sus titulares, y crea el objeto Tarjeta.
 *
 * Relaciones:
 * - Implementa: EventoFactory
 * - Composicion con: Random random (instancia propia para aleatoriedad)
 * - Asociacion con: (ninguna persistente)
 * - Usada por (dependencia): ContextoEvento (lee equipos local/visitante),
 *   Equipo (obtiene titulares), MotorSimulacion (la tiene en su lista de factories)
 * - Crea (Creator GRASP): Tarjeta
 *
 * GRASP:
 * - Creator (GRASP Creator): cumple porque tiene toda la informacion necesaria
 *   para crear una Tarjeta: el minuto, el jugador y el equipo, derivados del ContextoEvento.
 */
public class TarjetaFactory implements EventoFactory {
    private static final double PROB_BASE = 0.02;
    private final Random random = new Random();

    @Override
    public Optional<EventoPartido> crearEvento(ContextoEvento ctx) {
        if (random.nextDouble() > PROB_BASE) return Optional.empty();

        Equipo equipo = random.nextBoolean() ? ctx.getLocal() : ctx.getVisitante();
        List<IJugador> titulares = equipo.getTitulares();
        if (titulares.isEmpty()) return Optional.empty();

        IJugador jugador = titulares.get(random.nextInt(titulares.size()));
        return Optional.of(new Tarjeta(ctx.getMinuto(), jugador, equipo));
    }
}
