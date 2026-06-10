package simulador.facade;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.EquipoFixture;
import simulador.motor.MotorSimulacion;
import simulador.repositorio.RepositorioTorneo;
import simulador.strategy.TacticaStrategy;

public class TorneoFacade {
    private static final String NOMBRE_TORNEO_BASE = "SkyFut Champions 2026";
    private static final String FASE_OCTAVOS = "Octavos de Final";
    private static final int EQUIPOS_OCTAVOS = 16;

    private final RepositorioTorneo repositorioTorneo;
    private final MotorSimulacion motor;

    public TorneoFacade() {
        this.repositorioTorneo = new RepositorioTorneo();
        this.motor = new MotorSimulacion();
    }

    public void iniciarTorneo() {
        int idTorneo = repositorioTorneo.crearTorneo(generarNombreTorneo());
        int idFaseOctavos = repositorioTorneo.crearFase(idTorneo, FASE_OCTAVOS, 1);

        List<EquipoFixture> equipos = repositorioTorneo.listarEquiposConPlantelCompleto(EQUIPOS_OCTAVOS);
        if (equipos.size() != EQUIPOS_OCTAVOS) {
            throw new IllegalStateException("Se necesitan exactamente 16 equipos para generar octavos");
        }

        Collections.shuffle(equipos);
        repositorioTorneo.guardarPartidosIniciales(idFaseOctavos, equipos);
    }

    public void simularPartido(Partido partido) {
        motor.simularPartido(partido);
    }

    public void simularSiguientePartido() {
        throw new UnsupportedOperationException("Falta cargar el siguiente partido desde RepositorioPartido");
    }

    private String generarNombreTorneo() {
        return NOMBRE_TORNEO_BASE + " - " + UUID.randomUUID().toString().substring(0, 8);
    }

    public List<Equipo> cargarEquipos() {
        return Collections.emptyList();
    }

    public Equipo seleccionarEquipo(int id) {
        return null;
    }

    public void realizarCambio(Equipo equipo, IJugador sale, IJugador entra) {
        equipo.sustituir(sale, entra);
    }

    public void cambiarTactica(Equipo equipo, TacticaStrategy tactica) {
        equipo.setTactica(tactica);
    }
}
