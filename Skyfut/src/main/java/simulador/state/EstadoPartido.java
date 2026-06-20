package simulador.state;

import simulador.composite.Partido;

/**
 * [PATRON: State — State]
 *
 * Que hace: Define la interfaz para todos los estados posibles de un partido.
 * Cada estado concreto implementa iniciar(), avanzar(), permiteCambios() y
 * permiteEventosJuego() de forma diferente, restringiendo las acciones disponibles
 * segun el momento del partido. El Context (Partido) delega estas decisiones al
 * estado actual, evitando condicionales largos en el propio Partido.
 *
 * Relaciones:
 * - Implementada por: NoIniciado, PrimerTiempo, Entretiempo, SegundoTiempo, Finalizado
 * - Composicion con: (ninguna — es interfaz pura)
 * - Asociacion con: Partido (recibido como parametro en iniciar() y avanzar())
 * - Usada por (dependencia): Partido (lo referencia como campo estado),
 *   MotorSimulacion (consulta permiteEventosJuego()),
 *   TorneoFacade y GestorPartido (consultan permiteCambios())
 * - Crea (Creator GRASP): (no aplica — es interfaz)
 *
 * GRASP:
 * - ISP (Interface Segregation Principle): cumple porque define exactamente los
 *   metodos necesarios para modelar el comportamiento dependiente del estado.
 * - Polimorfismo: cumple porque permite que Partido delegue iniciar/avanzar/permitir
 *   sin conocer el estado concreto actual.
 */
public interface EstadoPartido {
    void iniciar(Partido p);
    void avanzar(Partido p);
    boolean permiteCambios();
    boolean permiteEventosJuego();
    String getNombre();
}
