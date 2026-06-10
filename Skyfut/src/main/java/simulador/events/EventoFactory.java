package simulador.events;

import java.util.Optional;

import simulador.dto.ContextoEvento;

public interface EventoFactory {
    public Optional<EventoPartido> crearEvento(ContextoEvento ctx);

}