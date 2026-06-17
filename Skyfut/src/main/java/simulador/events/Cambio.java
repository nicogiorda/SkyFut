package simulador.events;
import java.util.List;
import java.util.Optional;
import java.util.Random;    
import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;
import simulador.events.EventoFactory;
import simulador.composite.Partido;

// Cambio
public class Cambio implements EventoPartido {
    private final int minuto;
    private final IJugador sale;
    private final IJugador entra;
    private final Equipo equipo;

    public Cambio(int minuto, IJugador sale, IJugador entra, Equipo equipo) {
        this.minuto = minuto;
        this.sale = sale;
        this.entra = entra;
        this.equipo = equipo;
    }

    @Override
    public void aplicar(Partido partido) {
        // Decorator: el jugador que entra arranca limpio
        // el que sale pierde su lugar en titulares
        equipo.sustituir(sale, entra);
    }

    public IJugador getSale() { return sale; }
    public IJugador getEntra() { return entra; }
    public Equipo getEquipo() { return equipo; }

    @Override public int getMinuto() { return minuto; }
    @Override public String getTipo() { return "CAMBIO"; }
    @Override public String getDescripcion() {
        return entra.getNombre() + " entra por " + sale.getNombre() +
               " (" + equipo.getNombre() + ") min. " + minuto;
    }
}