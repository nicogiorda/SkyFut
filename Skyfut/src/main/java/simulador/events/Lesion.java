package simulador.events;

import simulador.composite.Partido;
import simulador.decorator.LesionDecorator;
import simulador.domain.Equipo;
import simulador.domain.IJugador;

public class Lesion implements EventoPartido {
    private final int minuto;
    private final IJugador jugador;
    private final Equipo equipo;

    public Lesion(int minuto, IJugador jugador, Equipo equipo) {
        this.minuto = minuto;
        this.jugador = jugador;
        this.equipo = equipo;
    }

    @Override
    public void aplicar(Partido partido) {
        Equipo equipoPartido = equipo.equals(partido.getLocal()) ? partido.getLocal() : partido.getVisitante();
        equipoPartido.decorarTitular(jugador, LesionDecorator::new);
    }

    @Override
    public int getMinuto() {
        return minuto;
    }

    @Override
    public String getTipo() {
        return "LESION";
    }

    @Override
    public String getDescripcion() {
        return "Lesion de " + jugador.getNombre() + " (" + equipo.getNombre() + ") min. " + minuto;
    }
}
