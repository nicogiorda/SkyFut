package simulador.events;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;


public class CambioFactory implements EventoFactory {
    private static final double PROBABILIDAD_CAMBIO = 0.015;
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

        if (!ctx.isEsSegundoTiempo() || ctx.getMinuto() < 60 || random.nextDouble() >= PROBABILIDAD_CAMBIO) {
            return Optional.empty();
        }

        Equipo equipoCambio = random.nextBoolean() ? ctx.getLocal() : ctx.getVisitante();
        List<IJugador> titulares = equipoCambio.getTitulares();
        List<IJugador> suplentes = equipoCambio.getSuplentes();
        if (titulares.isEmpty() || suplentes.isEmpty()) {
            return Optional.empty();
        }

        IJugador jugadorSale = titulares.get(random.nextInt(titulares.size()));
        IJugador jugadorEntra = suplentes.get(random.nextInt(suplentes.size()));
        return Optional.of(new Cambio(ctx.getMinuto(), jugadorSale, jugadorEntra, equipoCambio));
    }
}
