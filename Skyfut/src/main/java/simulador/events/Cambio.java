package simulador.events;
import java.util.List;
import java.util.Optional;
import java.util.Random;    
import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;
import simulador.events.EventoFactory;
import simulador.composite.Partido;

/**
 * [PATRON: Factory Method — ConcreteProduct]
 *
 * Que hace: Representa el evento de una sustitucion de jugadores durante el partido.
 * Al aplicarse llama a equipo.sustituir(sale, entra), moviendo al jugador saliente
 * de titulares a suplentes y al entrante de suplentes a titulares, en memoria.
 * El jugador que entra arranca sin ningun decorator acumulado.
 *
 * Relaciones:
 * - Implementa: EventoPartido
 * - Composicion con: (ninguna — solo referencias)
 * - Asociacion con: IJugador sale (sale del campo), IJugador entra (entra al campo),
 *   Equipo equipo (el equipo que realiza el cambio)
 * - Usada por (dependencia): Partido (recibe aplicar()), CambioFactory (la instancia),
 *   CalculadorEstadisticas (la detecta via instanceof para calcular minutos jugados),
 *   RepositorioPartido (la persiste via instanceof)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Information Expert: cumple porque es quien sabe como aplicarse sobre un Partido:
 *   delegar la sustitucion al equipo que conoce su propio plantel.
 */
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