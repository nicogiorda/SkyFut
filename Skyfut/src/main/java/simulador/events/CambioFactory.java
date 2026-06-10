package simulador.events;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;


public class CambioFactory implements EventoFactory {
    private final IJugador sale;
    private final IJugador entra;
    private final Equipo equipo;

    public CambioFactory(IJugador sale, IJugador entra, Equipo equipo) {
        this.sale = sale;
        this.entra = entra;
        this.equipo = equipo;
    }

    @Override
    public Optional<EventoPartido> crearEvento(ContextoEvento ctx) {
        // valida que haya suplentes
        if (equipo.getSuplentes().isEmpty()) return Optional.empty();
        return Optional.of(new Cambio(ctx.getMinuto(), sale, entra, equipo));
    }
}