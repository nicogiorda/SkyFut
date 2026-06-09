package simulador.torneo.events;

import simulador.composite.Partido;

public interface EventoPartido {
    int getMinuto();
    String getDescripcion();
    void aplicar(Partido partido);
}
