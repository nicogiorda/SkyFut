package simulador.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import simulador.strategy.TacticaStrategy;

public class Equipo {
    private String nombre;
    private List<IJugador> titulares;
    private List<IJugador> suplentes;
    private TacticaStrategy tactica;

    public Equipo(String nombre) {
        this(nombre, new ArrayList<>(), new ArrayList<>(), null);
    }

    public Equipo(String nombre, List<IJugador> titulares, List<IJugador> suplentes, TacticaStrategy tactica) {
        this.nombre = nombre;
        this.titulares = new ArrayList<>(titulares);
        this.suplentes = new ArrayList<>(suplentes);
        this.tactica = tactica;
    }

    public String getNombre() {
        return nombre;
    }

    public List<IJugador> getTitulares() {
        return titulares;
    }

    public List<IJugador> getSuplentes() {
        return suplentes;
    }

    public TacticaStrategy getTactica() {
        return tactica;
    }

    public void setTactica(TacticaStrategy tactica) {
        this.tactica = tactica;
    }

    public double getRendimientoTotal() {
        return titulares.stream()
                .mapToDouble(IJugador::getRendimiento)
                .sum();
    }

    public void decorarTitular(IJugador jugador, Function<IJugador, IJugador> decorador) {
        int indice = titulares.indexOf(jugador);
        if (indice >= 0) {
            titulares.set(indice, decorador.apply(jugador));
        }
    }

    public void sustituir(IJugador sale, IJugador entra) {
        int indiceTitular = titulares.indexOf(sale);
        if (indiceTitular < 0 || !suplentes.contains(entra)) {
            throw new IllegalArgumentException("El cambio debe ser entre un titular y un suplente del equipo");
        }

        titulares.set(indiceTitular, entra);
        suplentes.remove(entra);
        suplentes.add(sale);
    }
}
