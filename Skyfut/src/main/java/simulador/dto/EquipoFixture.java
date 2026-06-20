package simulador.dto;

/**
 * [PATRON: (ninguno) — DTO minimo (Java record)]
 *
 * Que hace: Proyeccion minima de un equipo para uso interno en la creacion de fases
 * y fixtures. Lleva solo el id del equipo y el id de su tactica, evitando cargar
 * el objeto Equipo completo (con plantel) cuando solo se necesitan estos dos datos
 * para insertar partidos en la base de datos.
 *
 * Relaciones:
 * - Hereda de: (Java record — extiende implicitamente java.lang.Record)
 * - Composicion con: (ninguna)
 * - Asociacion con: (ninguna — solo ints)
 * - Usada por (dependencia): RepositorioTorneo (la crea en listarEquiposConPlantelCompleto()
 *   y listarGanadoresFase()), GestorTorneo (la usa para iniciarTorneo()),
 *   GestorFases (la usa para crear la siguiente fase con los ganadores)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque permite trabajar con equipos a nivel de ID
 *   sin necesidad de instanciar objetos Equipo completos con sus listas de jugadores,
 *   reduciendo consultas a la base de datos.
 */
public record EquipoFixture(int idEquipo, int idTactica) {
}
