package simulador.torneo.events;

import simulador.torneo.domain.IJugador;

public class Gol implements EventoPartido {
    private final int minuto;
    private final IJugador autor;

    public Gol(int minuto, IJugador autor) {
        this.minuto = minuto;
        this.autor = autor;
    }

    @Override
    public int getMinuto() {
        return minuto;
    }

    @Override
    public String getTipo() {
        return "GOL";
    }

    @Override
    public String getDescripcion() {
        return "Gol de " + autor.getNombre() + " (min. " + minuto + ")";
    }
}