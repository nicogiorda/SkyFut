package simulador.state;

import simulador.composite.Partido;

public class Entretiempo implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha comenzado");
    }

    @Override
    public void avanzar(Partido p) {
        p.setEstado(new SegundoTiempo());
    }

    @Override
    public boolean permiteCambios() {
        return true;
    }

    @Override
    public boolean permiteEventosJuego() {
        return false;
    }

    @Override
    public String getNombre() {
        return "Entretiempo";
    }
}
