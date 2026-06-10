package simulador.events;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.domain.IJugador;

public class Gol implements EventoPartido {
    private final int minuto;
    private final IJugador autor;
    private final Equipo equipo;

    public Gol(int minuto, IJugador autor, Equipo equipo) {
        this.minuto = minuto;
        this.autor = autor;
        this.equipo = equipo;
    }

    @Override
    public void aplicar(Partido partido) {
        if (equipo.equals(partido.getLocal())) {
            partido.incrementarGolesLocal();
        } else if (equipo.equals(partido.getVisitante())) {
            partido.incrementarGolesVisitante();
        }
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

    public Equipo getEquipo() {
        return equipo;
    }
    
}