package simulador.events;

import simulador.composite.Partido;
import simulador.decorator.GolDecorator;
import simulador.domain.Equipo;
import simulador.domain.IJugador;

/**
 * [PATRON: Factory Method — ConcreteProduct]
 *
 * Que hace: Representa el evento de un gol ocurrido en un partido. Al aplicarse
 * incrementa el marcador del equipo que anotó y envuelve al autor en un GolDecorator
 * (bonus de rendimiento +20%). Conoce al autor (IJugador) y al equipo que anotó.
 *
 * Relaciones:
 * - Implementa: EventoPartido
 * - Composicion con: (ninguna — solo referencias)
 * - Asociacion con: IJugador autor (el goleador), Equipo equipo (el equipo que anotó)
 * - Usada por (dependencia): Partido (recibe aplicar()), GolFactory (la instancia),
 *   CalculadorEstadisticas (la detecta via instanceof para incrementar goles),
 *   RepositorioPartido (la persiste via instanceof)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Information Expert: cumple porque es quien sabe como aplicarse sobre un Partido:
 *   incrementar el marcador correspondiente y aplicar el decorator al autor.
 */
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

        Equipo equipoPartido = equipo.equals(partido.getLocal())
                ? partido.getLocal()
                : partido.getVisitante();
        equipoPartido.decorarTitular(autor, GolDecorator::new);
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

    public IJugador getAutor() {
        return autor;
    }

    public Equipo getEquipo() {
        return equipo;
    }
}
