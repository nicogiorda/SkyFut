package simulador.torneo.events;

public interface EventoFactory {
    EventoPartido crear(Object... params);
}