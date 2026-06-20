package simulador.composite;

import java.util.ArrayList;
import java.util.List;

import simulador.dto.Goleador;
import simulador.dto.ResultadoPartido;

/**
 * [PATRON: Composite — Composite (nodo raiz)]
 *
 * Que hace: Representa el torneo completo como nodo raiz del arbol Composite.
 * Contiene una lista de Fase y delega todas las operaciones (getResultados,
 * estaCompleto, getGoleadores) a cada una de sus fases, acumulando los resultados.
 * estaCompleto() solo es true si hay al menos una fase y todas estan completas.
 *
 * Relaciones:
 * - Implementa: ComponenteTorneo
 * - Composicion con: List<Fase> fases (el torneo posee y gestiona sus fases)
 * - Asociacion con: (ninguna adicional)
 * - Usada por (dependencia): GestorTorneo (trabaja con el modelo en memoria,
 *   aunque la persistencia va por RepositorioTorneo)
 * - Crea (Creator GRASP): (no aplica directamente)
 *
 * GRASP:
 * - Alta Cohesion: cumple porque toda su responsabilidad es gestionar la coleccion
 *   de fases y delegar consultas hacia abajo en el arbol.
 */
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
