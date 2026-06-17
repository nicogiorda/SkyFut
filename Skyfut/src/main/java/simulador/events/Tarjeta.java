package simulador.events;
import java.util.List;
import java.util.Optional;
import simulador.composite.Partido;
import simulador.decorator.TarjetaAmarillaDecorator;
import simulador.domain.Equipo;
import simulador.domain.IJugador;


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
