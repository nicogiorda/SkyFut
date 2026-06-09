package simulador.composite;

import java.util.List;

import simulador.torneo.dto.Goleador;
import simulador.torneo.dto.ResultadoPartido;

public interface ComponenteTorneo {
    List<ResultadoPartido> getResultados();
    boolean estaCompleto();
    List<Goleador> getGoleadores();
}
