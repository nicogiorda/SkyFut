package simulador.events;

import simulador.composite.Partido;
import simulador.decorator.LesionDecorator;
import simulador.domain.Equipo;
import simulador.domain.IJugador;

/**
 * [PATRON: Factory Method — ConcreteProduct]
 *
 * Que hace: Representa el evento de una lesion sufrida por un jugador durante
 * el partido. Al aplicarse envuelve al jugador afectado en un LesionDecorator,
 * reduciendo su rendimiento al 30% del actual. El jugador sigue en cancha pero
 * gravemente limitado.
 *
 * Relaciones:
 * - Implementa: EventoPartido
 * - Composicion con: (ninguna — solo referencias)
 * - Asociacion con: IJugador jugador (el lesionado), Equipo equipo (su equipo)
 * - Usada por (dependencia): Partido (recibe aplicar()), LesionFactory (la instancia),
 *   CalculadorEstadisticas (la detecta via instanceof para marcar lesionado=true),
 *   RepositorioPartido (la persiste via instanceof)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Information Expert: cumple porque es quien sabe como aplicarse sobre un Partido:
 *   localizar al jugador en el equipo correcto y aplicar el decorator de lesion.
 */
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

    public IJugador getJugador() { return jugador; }
    public Equipo getEquipo() { return equipo; }

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
