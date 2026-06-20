package simulador.composite;

import java.util.ArrayList;
import java.util.List;

import simulador.dto.Goleador;
import simulador.dto.ResultadoPartido;

/**
 * [PATRON: Composite — Composite (nodo intermedio)]
 *
 * Que hace: Representa una fase del torneo (Octavos, Cuartos, Semifinal, Final)
 * como nodo intermedio del arbol Composite. Contiene una lista de Partido y delega
 * todas las operaciones (getResultados, estaCompleto, getGoleadores) a cada partido,
 * acumulando los resultados. estaCompleto() solo es true si hay al menos un partido
 * y todos estan finalizados.
 *
 * Relaciones:
 * - Implementa: ComponenteTorneo
 * - Composicion con: List<Partido> partidos (la fase posee y gestiona sus partidos)
 * - Asociacion con: (ninguna adicional)
 * - Usada por (dependencia): Torneo (la contiene en su lista de fases),
 *   GestorFases (verifica si esta completa para crear la siguiente),
 *   RepositorioTorneo (la crea y persiste en BD)
 * - Crea (Creator GRASP): (no aplica directamente)
 *
 * GRASP:
 * - Alta Cohesion: cumple porque toda su responsabilidad es gestionar la coleccion
 *   de partidos y delegar consultas hacia las hojas del arbol.
 */
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
