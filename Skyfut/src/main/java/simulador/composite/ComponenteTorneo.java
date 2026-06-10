package simulador.composite;

import java.util.List;

import simulador.dto.Goleador;
import simulador.dto.ResultadoPartido;

public interface ComponenteTorneo {
    List<ResultadoPartido> getResultados();
    boolean estaCompleto();
    List<Goleador> getGoleadores();
}
