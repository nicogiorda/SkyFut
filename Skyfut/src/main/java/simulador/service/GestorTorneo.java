package simulador.service;

import java.util.Collections;
import java.util.List;

import simulador.domain.Equipo;
import simulador.dto.EquipoFixture;
import simulador.dto.EstadisticaJugadorTorneo;
import simulador.dto.FixturePartido;
import simulador.dto.ResumenTorneo;
import simulador.repositorio.RepositorioPartido;
import simulador.repositorio.RepositorioTorneo;

/**
 * [PATRON: (ninguno) — Servicio de ciclo de vida del torneo]
 *
 * Que hace: Gestiona el ciclo de vida del torneo: creacion (con shuffle de los 16
 * equipos y generacion de Octavos de Final), consulta de resultados, fixture,
 * campeon y estadisticas del equipo DT. Delega toda la persistencia a los
 * repositorios; no tiene logica de simulacion ni accede directamente a SQLite.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Agregacion con: RepositorioTorneo repositorioTorneo,
 *   RepositorioPartido repositorioPartido
 * - Usada por (dependencia): TorneoFacade (la delega en iniciarTorneo,
 *   consultarResultados, consultarFixture, consultarCampeon, consultarEstadisticasEquipoDt)
 * - Crea (Creator GRASP): (no aplica directamente — delega creacion a repositorios)
 *
 * GRASP:
 * - Alta Cohesion: cumple porque todas sus responsabilidades estan relacionadas
 *   con el ciclo de vida del torneo (crear, consultar, finalizar).
 * - Bajo Acoplamiento: cumple porque solo depende de los repositorios (abstracciones
 *   de persistencia) y de DTOs, no de la logica de simulacion.
 */
public class GestorTorneo {
    private static final String FASE_OCTAVOS = "Octavos de Final";
    private static final int EQUIPOS_OCTAVOS = 16;

    private final RepositorioTorneo repositorioTorneo;
    private final RepositorioPartido repositorioPartido;

    public GestorTorneo(RepositorioTorneo repositorioTorneo, RepositorioPartido repositorioPartido) {
        this.repositorioTorneo = repositorioTorneo;
        this.repositorioPartido = repositorioPartido;
    }

    public int iniciarTorneo(String nombreTorneo, Equipo equipoDt) {
        int idTorneo = repositorioTorneo.crearTorneo(nombreTorneo);
        if (equipoDt != null) {
            repositorioTorneo.asignarEquipoDT(idTorneo, equipoDt.getId());
        }

        int idFaseOctavos = repositorioTorneo.crearFase(idTorneo, FASE_OCTAVOS, 1);
        List<EquipoFixture> equipos = repositorioTorneo.listarEquiposConPlantelCompleto(EQUIPOS_OCTAVOS);
        if (equipos.size() != EQUIPOS_OCTAVOS) {
            throw new IllegalStateException("Se necesitan exactamente 16 equipos para generar octavos");
        }

        Collections.shuffle(equipos);
        repositorioTorneo.guardarPartidosIniciales(idFaseOctavos, equipos);
        return idTorneo;
    }

    public ResumenTorneo consultarResultados(int idTorneo, String nombreTorneo) {
        return repositorioPartido.consultarResumen(idTorneo, nombreTorneo);
    }

    public List<FixturePartido> consultarFixture(int idTorneo) {
        return repositorioPartido.listarFixture(idTorneo);
    }

    public String consultarCampeon(int idTorneo) {
        return repositorioTorneo.buscarNombreCampeon(idTorneo).orElse(null);
    }

    public List<EstadisticaJugadorTorneo> consultarEstadisticasEquipo(int idTorneo, int idEquipo) {
        return repositorioPartido.listarEstadisticasEquipo(idTorneo, idEquipo);
    }

    public void limpiarEstadisticas(int idTorneo) {
        repositorioPartido.limpiarEstadisticasTorneo(idTorneo);
    }

    public void asignarEquipoDT(int idTorneo, Equipo equipo) {
        repositorioTorneo.asignarEquipoDT(idTorneo, equipo.getId());
    }
}
