package simulador.decorator;

import simulador.domain.IJugador;

/**
 * [PATRON: Decorator — Decorator abstracto]
 *
 * Que hace: Clase abstracta que sirve de base para todos los decorators de jugadores.
 * Implementa IJugador y delega por defecto todos los metodos al componente envuelto
 * (jugador). Las subclases concretas sobreescriben getRendimiento() para modificar
 * el valor base. Provee getJugadorDecorado() para introspeccion de la cadena de wrappers.
 *
 * Relaciones:
 * - Implementa: IJugador (actua como un jugador mas desde el exterior)
 * - Agregacion con: IJugador jugador (referencia al componente envuelto; puede ser
 *   otro decorator o el Jugador base — no lo posee exclusivamente)
 * - Extendida por: CansancioDecorator, GolDecorator, LesionDecorator, TarjetaAmarillaDecorator
 * - Usada por (dependencia): Equipo.decorarTitular() (aplica los decorators en la lista de titulares)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Polimorfismo: cumple porque permite que cualquier subclase redefina getRendimiento()
 *   sin que los clientes sepan que tipo especifico de decorator estan usando.
 * - Bajo Acoplamiento: cumple porque solo depende de la interfaz IJugador, no de Jugador
 *   ni de ninguna clase concreta.
 */
public abstract class JugadorDecorator implements IJugador {
    protected IJugador jugador;

    protected JugadorDecorator(IJugador jugador) {
        this.jugador = jugador;
    }

    @Override
    public int getId() { return jugador.getId(); }

    @Override
    public String getNombre() {
        return jugador.getNombre();
    }

    @Override
    public String getPosicion() {
        return jugador.getPosicion();
    }

    @Override
    public double getRendimiento() {
        return jugador.getRendimiento();
    }

    public IJugador getJugadorDecorado() {
        return jugador;
    }

    public abstract String getNombreDecorador();

    public abstract String getImpactoDecorador();
}
