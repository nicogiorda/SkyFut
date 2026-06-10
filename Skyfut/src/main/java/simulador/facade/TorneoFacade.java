package simulador.facade;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.persistence.DatabaseConnection;
import simulador.strategy.TacticaStrategy;

public class TorneoFacade {
    private final DatabaseConnection db;

    public TorneoFacade() throws SQLException {
        this.db = DatabaseConnection.getInstance();
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