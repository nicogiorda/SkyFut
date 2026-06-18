package simulador.service;

import java.util.List;

import simulador.composite.Partido;
import simulador.dto.EquipoFixture;
import simulador.repositorio.RepositorioPartido;
import simulador.repositorio.RepositorioTorneo;

public class GestorFases {
    private final RepositorioTorneo repositorioTorneo;
    private final RepositorioPartido repositorioPartido;

    public GestorFases(RepositorioTorneo repositorioTorneo, RepositorioPartido repositorioPartido) {
        this.repositorioTorneo = repositorioTorneo;
        this.repositorioPartido = repositorioPartido;
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
            repositorioPartido.limpiarEstadisticasTorneo(idTorneo);
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
