package simulador.strategy;

/**
 * [PATRON: Strategy — Strategy]
 *
 * Que hace: Define la interfaz para las distintas tacticas de juego. Cada tactica
 * concreta retorna modificadores de ataque y defensa que se aplican al rendimiento
 * total del equipo para calcular las probabilidades de eventos. Permite cambiar
 * la estrategia de juego de un equipo en tiempo de ejecucion (durante el entretiempo)
 * sin que Equipo ni ContextoEvento necesiten conocer la tactica concreta.
 *
 * Relaciones:
 * - Implementada por: TacticaOfensiva, TacticaDefensiva, TacticaEquilibrada
 * - Composicion con: (ninguna — es interfaz pura)
 * - Asociacion con: (ninguna directa)
 * - Usada por (dependencia): Equipo (tiene una referencia asociada, puede cambiarse),
 *   ContextoEvento (la consulta en getRendimientoAtaque/Defensa()),
 *   RepositorioEquipo (crea la concreta correcta segun BD),
 *   SkyFutFrame (permite seleccionarla al DT)
 * - Crea (Creator GRASP): (no aplica — es interfaz)
 *
 * GRASP:
 * - ISP (Interface Segregation Principle): cumple porque define solo los tres metodos
 *   necesarios para una tactica de juego.
 * - Polimorfismo: cumple porque Equipo usa TacticaStrategy y no la clase concreta,
 *   permitiendo cambiar la tactica sin modificar Equipo ni ContextoEvento.
 * - Bajo Acoplamiento: cumple porque Equipo no sabe si tiene una TacticaOfensiva o
 *   Defensiva; solo conoce la interfaz.
 */
public interface TacticaStrategy {
    double getModificadorAtaque();
    double getModificadorDefensa();
    String getFormacion();
}
