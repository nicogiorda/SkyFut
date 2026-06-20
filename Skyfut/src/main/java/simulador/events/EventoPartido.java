package simulador.events;

import simulador.composite.Partido;

/**
 * [PATRON: Factory Method — Product]
 *
 * Que hace: Define la interfaz para todos los eventos que pueden ocurrir durante
 * un partido (Gol, Tarjeta, Lesion, Cambio). Cada evento sabe en que minuto ocurrio,
 * que tipo es, como describirse, y como aplicarse sobre el Partido (modificando
 * su estado: marcadores, decorators de jugadores, sustituciones).
 *
 * Relaciones:
 * - Implementada por: Gol, Tarjeta, Lesion, Cambio (ConcreteProducts del Factory Method)
 * - Composicion con: (ninguna — es interfaz pura)
 * - Asociacion con: (ninguna directa)
 * - Usada por (dependencia): Partido (los registra en su lista de eventos),
 *   MotorSimulacion (los aplica y registra), CalculadorEstadisticas (los itera),
 *   RepositorioPartido (los persiste)
 * - Crea (Creator GRASP): (no aplica — es interfaz)
 *
 * GRASP:
 * - ISP (Interface Segregation Principle): cumple porque define solo los metodos
 *   minimos necesarios para un evento de partido.
 * - Polimorfismo: cumple porque permite que el motor y el repositorio traten
 *   uniformemente a Gol, Tarjeta, Lesion y Cambio sin instanceof (salvo en
 *   casos puntuales de persistencia).
 */
public interface EventoPartido {
    int getMinuto();
    String getTipo();
    String getDescripcion();

    void aplicar(Partido partido);
}
