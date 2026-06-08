package simulador.torneo.domain;

import simulador.torneo.strategy.TacticaStrategy;
import java.util.List;

public class Equipo {
    private String nombre;
    private List<IJugador> titulares;
    private List<IJugador> suplentes;
    private TacticaStrategy tactica;

    public Equipo(String nombre) { this.nombre = nombre; }

    public void setTactica(TacticaStrategy tactica) { this.tactica = tactica; }
    public TacticaStrategy getTactica() { return tactica; }
    public List<IJugador> getTitulares() { return titulares; }
    public void sustituir(IJugador sale, IJugador entra) { /* TODO */ }
}
