package simulador.torneo.state;

import simulador.composite.Partido;

public interface EstadoPartido {
    void iniciar(Partido p);
    void avanzar(Partido p);
    boolean permiteCambios();
    boolean permiteEventosJuego();
    String getNombre();
}
