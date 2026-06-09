package simulador.composite;

import java.util.ArrayList;
import java.util.List;

import simulador.torneo.dto.Goleador;
import simulador.torneo.dto.ResultadoPartido;

public class Torneo implements ComponenteTorneo {
    private String nombre;
    private List<Fase> fases;

    public Torneo(String nombre) {
        this.nombre = nombre;
        this.fases = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Fase> getFases() {
        return fases;
    }

    public void agregarFase(Fase f) {
        fases.add(f);
    }

    @Override
    public List<ResultadoPartido> getResultados() {
        List<ResultadoPartido> resultados = new ArrayList<>();
        for (Fase fase : fases) {
            resultados.addAll(fase.getResultados());
        }
        return resultados;
    }

    @Override
    public boolean estaCompleto() {
        return !fases.isEmpty() && fases.stream().allMatch(Fase::estaCompleto);
    }

    @Override
    public List<Goleador> getGoleadores() {
        List<Goleador> goleadores = new ArrayList<>();
        for (Fase fase : fases) {
            goleadores.addAll(fase.getGoleadores());
        }
        return goleadores;
    }
}
