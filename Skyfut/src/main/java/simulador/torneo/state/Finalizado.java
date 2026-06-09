package simulador.torneo.state;

import simulador.composite.Partido;

public class Finalizado implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha finalizado");
    }

    @Override
    public void avanzar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha finalizado");
    }

    @Override
    public boolean permiteCambios() {
        return false;
    }

    @Override
    public boolean permiteEventosJuego() {
        return false;
    }

    @Override
    public String getNombre() {
        return "Finalizado";
    }
}
