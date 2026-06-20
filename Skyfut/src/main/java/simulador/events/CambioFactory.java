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
 * Que hace: Crea eventos de tipo Cambio en dos modos distintos:
 * (1) Modo manual (DT): construido con sale/entra/equipo especificos, genera el
 * cambio exactamente como lo indico el DT durante el entretiempo.
 * (2) Modo automatico: construido sin parametros, solo actua en el minuto 45
 * con una probabilidad del 65%, eligiendo jugadores al azar para el equipo rival.
 *
 * Relaciones:
 * - Implementa: EventoFactory
 * - Composicion con: Random random (instancia propia para aleatoriedad)
 * - Asociacion con: IJugador sale, IJugador entra, Equipo equipo
 *   (opcionales — null en modo automatico)
 * - Usada por (dependencia): ContextoEvento (lee minuto y equipos),
 *   MotorSimulacion (la tiene para cambios automaticos del entretiempo),
 *   GestorPartido (la instancia en modo manual para cambios del DT)
 * - Crea (Creator GRASP): Cambio
 *
 * GRASP:
 * - Creator (GRASP Creator): cumple porque tiene toda la informacion necesaria
 *   para crear un Cambio (jugadores que entran/salen y el equipo involucrado).
 */
public class CambioFactory implements EventoFactory {
    private static final double PROBABILIDAD_CAMBIO_ENTRETIEMPO = 0.65;
    private final Random random = new Random();
    private final IJugador sale;
    private final IJugador entra;
    private final Equipo equipo;

    public CambioFactory() {
        this.sale = null;
        this.entra = null;
        this.equipo = null;
    }

    public CambioFactory(IJugador sale, IJugador entra, Equipo equipo) {
        this.sale = sale;
        this.entra = entra;
        this.equipo = equipo;
    }

    @Override
    public Optional<EventoPartido> crearEvento(ContextoEvento ctx) {
        if (equipo != null && sale != null && entra != null) {
            if (equipo.getSuplentes().isEmpty()) return Optional.empty();
            return Optional.of(new Cambio(ctx.getMinuto(), sale, entra, equipo));
        }

        if (ctx.getMinuto() != 45 || ctx.isEsSegundoTiempo()) {
            return Optional.empty();
        }

        Equipo equipoCambio = random.nextBoolean() ? ctx.getLocal() : ctx.getVisitante();
        return crearCambioAutomatico(ctx.getMinuto(), equipoCambio);
    }

    public Optional<EventoPartido> crearCambioAutomatico(int minuto, Equipo equipoCambio) {
        if (random.nextDouble() >= PROBABILIDAD_CAMBIO_ENTRETIEMPO) {
            return Optional.empty();
        }

        List<IJugador> titulares = equipoCambio.getTitulares();
        List<IJugador> suplentes = equipoCambio.getSuplentes();
        if (titulares.isEmpty() || suplentes.isEmpty()) {
            return Optional.empty();
        }

        IJugador jugadorSale = titulares.get(random.nextInt(titulares.size()));
        IJugador jugadorEntra = suplentes.get(random.nextInt(suplentes.size()));
        return Optional.of(new Cambio(minuto, jugadorSale, jugadorEntra, equipoCambio));
    }
}
