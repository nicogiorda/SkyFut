package simulador.service;

import java.util.List;

import simulador.composite.Partido;
import simulador.dto.EquipoFixture;
import simulador.repositorio.RepositorioTorneo;

/**
 * [PATRON: (ninguno) — Servicio de progresion de fases]
 *
 * Que hace: Luego de cada partido, verifica si la fase actual se completo (todos
 * sus partidos finalizados). Si es asi, la marca como completa y decide el paso
 * siguiente: si queda un unico ganador, finaliza el torneo; si quedan mas equipos,
 * crea la siguiente fase (Octavos → Cuartos → Semifinal → Final) con los ganadores.
 * Toda la persistencia se delega a RepositorioTorneo.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Agregacion con: RepositorioTorneo repositorioTorneo
 * - Usada por (dependencia): GestorPartido (la llama en cerrarPartido() despues
 *   de persistir el resultado), EquipoFixture (como proyeccion de ganadores)
 * - Crea (Creator GRASP): (no aplica directamente — delega creacion a repositorioTorneo)
 *
 * GRASP:
 * - Alta Cohesion: cumple porque su unica responsabilidad es manejar la logica
 *   de avance entre fases del torneo eliminatorio.
 * - Bajo Acoplamiento: cumple porque solo depende de RepositorioTorneo y de
 *   Partido (para obtener el id del partido y consultar su fase).
 */
public class GestorFases {
    private final RepositorioTorneo repositorioTorneo;

    public GestorFases(RepositorioTorneo repositorioTorneo) {
        this.repositorioTorneo = repositorioTorneo;
    }

    public void avanzarFaseSiCorresponde(int idTorneo, Partido partido) {
        RepositorioTorneo.FaseInfo fase = repositorioTorneo.buscarFaseDePartido(partido.getId())
                .orElseThrow(() -> new IllegalStateException("No se encontro la fase del partido"));

        if (!repositorioTorneo.faseCompleta(fase.id())) {
            return;
        }

        repositorioTorneo.marcarFaseCompleta(fase.id());
        List<EquipoFixture> ganadores = repositorioTorneo.listarGanadoresFase(fase.id());

        if (ganadores.size() == 1) {
            repositorioTorneo.finalizarTorneo(idTorneo, ganadores.get(0).idEquipo());
            return;
        }

        int ordenSiguiente = fase.orden() + 1;
        if (repositorioTorneo.faseExiste(idTorneo, ordenSiguiente)) {
            return;
        }

        int idFaseSiguiente = repositorioTorneo.crearFase(
                idTorneo,
                nombreFaseSiguiente(ganadores.size()),
                ordenSiguiente);
        repositorioTorneo.guardarPartidosIniciales(idFaseSiguiente, ganadores);
    }

    private String nombreFaseSiguiente(int cantidadEquipos) {
        return switch (cantidadEquipos) {
            case 8 -> "Cuartos de Final";
            case 4 -> "Semifinal";
            case 2 -> "Final";
            default -> "Fase " + cantidadEquipos + " equipos";
        };
    }
}
