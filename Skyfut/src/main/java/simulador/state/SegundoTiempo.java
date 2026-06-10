package simulador.state;

import simulador.composite.Partido;

public class SegundoTiempo implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha comenzado");
    }

    @Override
    public void avanzar(Partido p) {
        p.setEstado(new Finalizado());
    }

    @Override
    public boolean permiteCambios() {
        return false;
    }

    @Override
    public boolean permiteEventosJuego() {
        return true;
    }

    @Override
    public String getNombre() {
        return "Segundo Tiempo";
    }
}
