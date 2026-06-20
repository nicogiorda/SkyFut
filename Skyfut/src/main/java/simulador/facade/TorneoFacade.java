package simulador.facade;

import java.util.List;
import java.util.UUID;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.EstadisticaJugadorTorneo;
import simulador.dto.FixturePartido;
import simulador.dto.ResumenTorneo;
import simulador.motor.MotorSimulacion;
import simulador.repositorio.RepositorioEquipo;
import simulador.repositorio.RepositorioPartido;
import simulador.repositorio.RepositorioTorneo;
import simulador.service.CalculadorEstadisticas;
import simulador.service.GestorFases;
import simulador.service.GestorPartido;
import simulador.service.GestorTorneo;
import simulador.strategy.TacticaStrategy;

/**
 * [PATRON: Facade — Facade]
 *
 * Que hace: Es el unico punto de entrada entre la capa de presentacion (SkyFutFrame)
 * y la logica de negocio/servicios. Simplifica la interfaz del subsistema
 * (GestorTorneo, GestorPartido, RepositorioEquipo) exponiendo operaciones de alto
 * nivel. Mantiene el estado de sesion: idTorneo, equipoDt y partidoActual.
 * Valida precondiciones (torneo iniciado, partido activo, estado permiteCambios)
 * antes de delegar a los servicios internos.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna — agrega servicios)
 * - Agregacion con: GestorTorneo (coordina ciclo de vida del torneo),
 *   GestorPartido (coordina simulacion de partidos),
 *   RepositorioEquipo (carga equipos disponibles)
 * - Asociacion con: Equipo equipoDt (el equipo seleccionado por el DT; puede ser null),
 *   Partido partidoActual (el partido en curso; puede ser null)
 * - Usada por (dependencia): SkyFutFrame (es la UNICA clase que la usa directamente)
 * - Crea (Creator GRASP): instancia internamente RepositorioTorneo, RepositorioPartido,
 *   GestorFases, GestorTorneo, GestorPartido, MotorSimulacion, CalculadorEstadisticas
 *
 * GRASP:
 * - Controller (GRASP Controller): cumple porque recibe los eventos de la UI
 *   (iniciarTorneo, simularPartido, realizarCambio, cambiarTactica) y los delega
 *   a los servicios correspondientes sin tener logica de negocio propia.
 * - Bajo Acoplamiento: cumple porque SkyFutFrame solo conoce TorneoFacade;
 *   no tiene referencias directas a GestorTorneo, MotorSimulacion, ni repositorios.
 */
public class TorneoFacade {
    private static final String NOMBRE_TORNEO_BASE = "SkyFut Champions 2026";

    private final RepositorioEquipo repositorioEquipo;
    private final GestorTorneo gestorTorneo;
    private final GestorPartido gestorPartido;

    private int idTorneo = -1;
    private String nombreTorneoActual = NOMBRE_TORNEO_BASE;
    private Equipo equipoDt;
    private Partido partidoActual;

    public TorneoFacade() {
        RepositorioTorneo repositorioTorneo = new RepositorioTorneo();
        this.repositorioEquipo = new RepositorioEquipo();
        RepositorioPartido repositorioPartido = new RepositorioPartido(repositorioEquipo);
        GestorFases gestorFases = new GestorFases(repositorioTorneo);
        this.gestorTorneo = new GestorTorneo(repositorioTorneo, repositorioPartido);
        this.gestorPartido = new GestorPartido(
                repositorioPartido,
                new MotorSimulacion(),
                new CalculadorEstadisticas(),
                gestorFases);
    }

    public void iniciarTorneo() {
        nombreTorneoActual = generarNombreTorneo();
        idTorneo = gestorTorneo.iniciarTorneo(nombreTorneoActual, equipoDt);
    }

    public Partido prepararSiguientePartido() {
        validarTorneoIniciado();
        partidoActual = gestorPartido.prepararSiguientePartido(idTorneo);
        return partidoActual;
    }

    public void simularSiguientePartido() {
        Partido partido = prepararSiguientePartido();
        gestorPartido.simularSiguientePartido(idTorneo, equipoDt, partido);
    }

    public void simularPrimerTiempoPartidoActual() {
        validarPartidoActual();
        gestorPartido.simularPrimerTiempo(partidoActual);
    }

    public void simularSegundoTiempoPartidoActual() {
        validarTorneoIniciado();
        validarPartidoActual();
        gestorPartido.simularSegundoTiempo(idTorneo, partidoActual, equipoDt);
    }

    public void simularRestoTorneoAutomatico() {
        validarTorneoIniciado();
        gestorPartido.simularRestoTorneoAutomatico(idTorneo, equipoDt);
    }

    public ResumenTorneo consultarResultados() {
        validarTorneoIniciado();
        return gestorTorneo.consultarResultados(idTorneo, nombreTorneoActual);
    }

    public List<FixturePartido> consultarFixture() {
        validarTorneoIniciado();
        return gestorTorneo.consultarFixture(idTorneo);
    }

    public String consultarCampeon() {
        validarTorneoIniciado();
        return gestorTorneo.consultarCampeon(idTorneo);
    }

    public List<EstadisticaJugadorTorneo> consultarEstadisticasEquipoDt() {
        validarTorneoIniciado();
        if (equipoDt == null) {
            throw new IllegalStateException("No hay un equipo del DT seleccionado");
        }
        return gestorTorneo.consultarEstadisticasEquipo(idTorneo, equipoDt.getId());
    }

    public void limpiarEstadisticasTorneoActual() {
        validarTorneoIniciado();
        gestorTorneo.limpiarEstadisticas(idTorneo);
    }

    public void realizarCambio(Equipo equipo, IJugador sale, IJugador entra) {
        validarPartidoActual();
        validarPermiteCambios("Los cambios solo estan permitidos durante el entretiempo");
        gestorPartido.realizarCambio(partidoActual, equipo, sale, entra);
    }

    public void cambiarTactica(Equipo equipo, TacticaStrategy tactica) {
        validarPartidoActual();
        validarPermiteCambios("El cambio de tactica solo esta permitido durante el entretiempo");
        gestorPartido.cambiarTactica(partidoActual, equipo, tactica);
    }

    public List<Equipo> cargarEquipos() {
        return repositorioEquipo.listarEquipos();
    }

    public Equipo seleccionarEquipo(int id) {
        equipoDt = repositorioEquipo.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un equipo con id " + id));
        if (idTorneo > 0) {
            gestorTorneo.asignarEquipoDT(idTorneo, equipoDt);
        }
        return equipoDt;
    }

    public Partido getPartidoActual() {
        return partidoActual;
    }

    public Equipo getEquipoDt() {
        return equipoDt;
    }

    public boolean torneoIniciado() {
        return idTorneo > 0;
    }

    public boolean partidoActualEsDelDT() {
        return gestorPartido.partidoEsDelDT(partidoActual, equipoDt);
    }

    public boolean partidoActualEstaEnEntretiempo() {
        return partidoActual != null && partidoActual.getEstado().permiteCambios();
    }

    public void reiniciarParaNuevoTorneo() {
        idTorneo = -1;
        nombreTorneoActual = NOMBRE_TORNEO_BASE;
        equipoDt = null;
        partidoActual = null;
    }

    private void validarTorneoIniciado() {
        if (idTorneo < 0) {
            throw new IllegalStateException("El torneo no ha sido iniciado");
        }
    }

    private void validarPartidoActual() {
        if (partidoActual == null) {
            throw new IllegalStateException("No hay un partido actual");
        }
    }

    private void validarPermiteCambios(String mensaje) {
        if (!partidoActual.getEstado().permiteCambios()) {
            throw new IllegalStateException(mensaje);
        }
    }

    private String generarNombreTorneo() {
        return NOMBRE_TORNEO_BASE + " - " + UUID.randomUUID().toString().substring(0, 8);
    }
}
