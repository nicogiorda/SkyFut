package simulador.events;

import simulador.composite.Partido;

public interface EventoPartido {
    int getMinuto();
    String getTipo();
    String getDescripcion();

    void aplicar(Partido partido);
}
