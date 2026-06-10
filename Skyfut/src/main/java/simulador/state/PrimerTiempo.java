package simulador.torneo.state;

import simulador.composite.Partido;

public class PrimerTiempo implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha comenzado");
    }

    @Override
    public void avanzar(Partido p) {
        p.setEstado(new Entretiempo());
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
        return "Primer Tiempo";
    }
}
