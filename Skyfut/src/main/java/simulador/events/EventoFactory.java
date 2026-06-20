package simulador.events;

import java.util.Optional;

import simulador.dto.ContextoEvento;

/**
 * [PATRON: Factory Method — Creator]
 *
 * Que hace: Define la interfaz de creacion de eventos para el patron Factory Method.
 * Cada factory concreta decide de forma independiente si genera o no su tipo de evento
 * segun el contexto del minuto actual. Optional.empty() significa "este minuto no
 * ocurrio este tipo de evento" (no es un error). La aleatoriedad vive dentro de
 * cada factory, no en el motor.
 *
 * Relaciones:
 * - Implementada por: GolFactory, TarjetaFactory, LesionFactory, CambioFactory
 * - Composicion con: (ninguna — es interfaz pura)
 * - Asociacion con: (ninguna directa)
 * - Usada por (dependencia): ContextoEvento (recibido como parametro),
 *   MotorSimulacion (la itera para generar eventos por minuto)
 * - Crea (Creator GRASP): EventoPartido (y subtipos concretos segun implementacion)
 *
 * GRASP:
 * - ISP (Interface Segregation Principle): cumple porque define un unico metodo
 *   de responsabilidad clara: decidir si crear un evento dado un contexto.
 * - Creator (GRASP Creator): cumple porque es la abstraccion responsable de la
 *   creacion de objetos EventoPartido.
 */
public interface EventoFactory {
    public Optional<EventoPartido> crearEvento(ContextoEvento ctx);

}