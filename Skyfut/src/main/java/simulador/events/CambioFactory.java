package simulador.events;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;


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
