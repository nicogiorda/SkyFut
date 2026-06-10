package simulador.torneo.facade;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import simulador.torneo.domain.Equipo;
import simulador.torneo.domain.IJugador;
import simulador.torneo.persistence.DatabaseConnection;
import simulador.torneo.strategy.TacticaStrategy;

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