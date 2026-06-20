package simulador.events;
import java.util.List;
import java.util.Optional;
import simulador.composite.Partido;
import simulador.decorator.TarjetaAmarillaDecorator;
import simulador.domain.Equipo;
import simulador.domain.IJugador;

/**
 * [PATRON: Factory Method — ConcreteProduct]
 *
 * Que hace: Representa el evento de una tarjeta amarilla recibida por un jugador.
 * Al aplicarse envuelve al jugador sancionado en un TarjetaAmarillaDecorator,
 * reduciendo su rendimiento en 0.10 para el resto del partido.
 *
 * Relaciones:
 * - Implementa: EventoPartido
 * - Composicion con: (ninguna — solo referencias)
 * - Asociacion con: IJugador jugador (el amonestado), Equipo equipo (su equipo)
 * - Usada por (dependencia): Partido (recibe aplicar()), TarjetaFactory (la instancia),
 *   CalculadorEstadisticas (la detecta via instanceof para incrementar tarjetas),
 *   RepositorioPartido (la persiste via instanceof)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Information Expert: cumple porque es quien sabe como aplicarse sobre un Partido:
 *   localizar al jugador en el equipo correcto y aplicar el decorator de amonestacion.
 */
public class Tarjeta implements EventoPartido {
    private final int minuto;
    private final IJugador jugador;
    private final Equipo equipo;

    public Tarjeta(int minuto, IJugador jugador, Equipo equipo) {
        this.minuto = minuto;
        this.jugador = jugador;
        this.equipo = equipo;
    }

    @Override
    public void aplicar(Partido partido) {
        Equipo eq = equipo.equals(partido.getLocal())
            ? partido.getLocal()
            : partido.getVisitante();

        // Lambda: le pasamos a decorarTitular las instrucciones de cómo decorar al jugador.
        // "j" representa al jugador que encontró en la lista de titulares.
        // La flecha "->" separa el parámetro (j) de lo que hay que hacer con él.
        // En este caso: envolver a j en un TarjetaAmarillaDecorator, que reduce su rendimiento.
        eq.decorarTitular(jugador, j -> new TarjetaAmarillaDecorator(j));
    }

    public IJugador getJugador() { return jugador; }
    public Equipo getEquipo() { return equipo; }

    @Override public int getMinuto() { return minuto; }
    @Override public String getTipo() { return "TARJETA"; }
    @Override public String getDescripcion() {
        return "Tarjeta amarilla a " + jugador.getNombre() +
               " (" + equipo.getNombre() + ") min. " + minuto;
    }
}
