package simulador.torneo.events;

import simulador.composite.Partido;

public interface EventoPartido {
    int getMinuto();
    String getTipo();
    String getDescripcion();

    default void aplicar(Partido partido) {
        // Intencionalmente vacío: los eventos pueden ser descriptivos o aplicar efectos más adelante.
    }
}
