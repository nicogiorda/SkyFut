package simulador.torneo.state;

import simulador.composite.Partido;

public class NoIniciado implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        p.setEstado(new PrimerTiempo());
    }

    @Override
    public void avanzar(Partido p) {
        throw new UnsupportedOperationException("El partido no ha comenzado");
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
        return "No Iniciado";
    }
}
