package simulador.composite;

import java.util.ArrayList;
import java.util.List;

import simulador.torneo.dto.Goleador;
import simulador.torneo.dto.ResultadoPartido;

public class Fase implements ComponenteTorneo {
    private String nombre;
    private List<Partido> partidos;

    public Fase(String nombre) {
        this.nombre = nombre;
        this.partidos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void agregarPartido(Partido p) {
        partidos.add(p);
    }

    @Override
    public List<ResultadoPartido> getResultados() {
        List<ResultadoPartido> resultados = new ArrayList<>();
        for (Partido partido : partidos) {
            resultados.addAll(partido.getResultados());
        }
        return resultados;
    }

    @Override
    public boolean estaCompleto() {
        return !partidos.isEmpty() && partidos.stream().allMatch(Partido::estaCompleto);
    }

    @Override
    public List<Goleador> getGoleadores() {
        List<Goleador> goleadores = new ArrayList<>();
        for (Partido partido : partidos) {
            goleadores.addAll(partido.getGoleadores());
        }
        return goleadores;
    }
}
