package simulador.domain;

/**
 * [PATRON: Decorator — Component]
 *
 * Que hace: Define la interfaz comun para todos los jugadores del sistema,
 * tanto el jugador base (Jugador) como los decoradores que modifican su
 * rendimiento (CansancioDecorator, LesionDecorator, etc.). Permite que los
 * decorators sean transparentes para cualquier clase que use jugadores.
 *
 * Relaciones:
 * - Implementada por: Jugador (ConcreteComponent), JugadorDecorator (Decorator abstracto)
 * - Composicion con: (ninguna — es interfaz pura)
 * - Asociacion con: (ninguna directa)
 * - Usada por (dependencia): Equipo (lista de titulares/suplentes), EstadisticasJugador,
 *   EventoPartido y sus implementaciones (Gol, Tarjeta, Lesion, Cambio),
 *   CalculadorEstadisticas, RepositorioJugador
 * - Crea (Creator GRASP): (no aplica — es interfaz)
 *
 * GRASP:
 * - ISP (Interface Segregation Principle): cumple porque define solo los metodos
 *   minimos necesarios para un jugador, sin obligar a implementar comportamientos
 *   no relacionados.
 * - Polimorfismo: cumple porque permite tratar de forma uniforme a Jugador y
 *   a cualquier decorator apilado sobre el, sin que el cliente conozca el tipo concreto.
 */
public interface IJugador {
    int getId();
    String getNombre();
    String getPosicion();
    double getRendimiento();
}
