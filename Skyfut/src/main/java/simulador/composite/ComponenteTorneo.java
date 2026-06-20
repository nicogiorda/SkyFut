package simulador.composite;

import java.util.List;

import simulador.dto.Goleador;
import simulador.dto.ResultadoPartido;

/**
 * [PATRON: Composite — Component]
 *
 * Que hace: Define la interfaz uniforme para todos los nodos del arbol del torneo
 * (Torneo, Fase, Partido). Permite consultar resultados, verificar si esta completo
 * y obtener goleadores en cualquier nivel del arbol con la misma interfaz,
 * sin que el cliente sepa si esta tratando con un nodo compuesto o una hoja.
 *
 * Relaciones:
 * - Implementada por: Torneo (Composite), Fase (Composite), Partido (Leaf)
 * - Composicion con: (ninguna — es interfaz pura)
 * - Asociacion con: (ninguna directa)
 * - Usada por (dependencia): ResultadoPartido y Goleador (retornados por sus metodos),
 *   GestorTorneo (consulta resultados), SkyFutFrame (muestra resultados)
 * - Crea (Creator GRASP): (no aplica — es interfaz)
 *
 * GRASP:
 * - ISP (Interface Segregation Principle): cumple porque define solo los tres metodos
 *   necesarios para consultar el estado del arbol del torneo.
 * - Polimorfismo: cumple porque permite que el codigo cliente trate uniformemente
 *   a Torneo, Fase y Partido sin conocer su tipo concreto.
 */
public interface ComponenteTorneo {
    List<ResultadoPartido> getResultados();
    boolean estaCompleto();
    List<Goleador> getGoleadores();
}
