package simulador.torneo.events;

import simulador.torneo.domain.IJugador;

public class GolFactory implements EventoFactory {
    @Override
    public EventoPartido crear(Object... params) {
        int minuto = (int) params[0];
        IJugador autor = (IJugador) params[1];
        return new Gol(minuto, autor);
    }
}