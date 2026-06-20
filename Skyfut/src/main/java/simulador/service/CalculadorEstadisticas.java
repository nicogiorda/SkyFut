package simulador.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.domain.EstadisticasJugador;
import simulador.domain.IJugador;
import simulador.events.Cambio;
import simulador.events.EventoPartido;
import simulador.events.Gol;
import simulador.events.Lesion;
import simulador.events.Tarjeta;

/**
 * [PATRON: (ninguno) — Servicio de calculo de estadisticas]
 *
 * Que hace: Calcula las estadisticas por jugador de un partido iterando sus eventos.
 * Para cada Gol incrementa goles del autor; para Tarjeta incrementa amarillas;
 * para Lesion marca lesionado=true; para Cambio ajusta los minutos jugados del
 * jugador que sale y registra al que entra. Finalmente actualiza el rendimientoFinal
 * de cada jugador segun su estado actual (con todos los decorators aplicados).
 * Retorna una List<EstadisticasJugador>, una por jugador que participo.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Agregacion con: (ninguna — sin estado propio)
 * - Usada por (dependencia): Partido (para leer sus eventos y jugadores),
 *   GestorPartido (la llama en cerrarPartido() y pasa el resultado a RepositorioPartido)
 * - Crea (Creator GRASP): EstadisticasJugador (una instancia por jugador del partido)
 *
 * GRASP:
 * - Information Expert: cumple porque es quien tiene acceso a todos los eventos del
 *   partido y puede calcular las estadisticas derivadas; ninguna otra clase tiene
 *   toda esa informacion concentrada de forma tan natural.
 */
public class CalculadorEstadisticas {
    public List<EstadisticasJugador> calcular(Partido partido) {
        Map<Integer, EstadisticasJugador> statsMap = new LinkedHashMap<>();
        registrarPlantelCompleto(statsMap, partido.getLocal());
        registrarPlantelCompleto(statsMap, partido.getVisitante());

        for (EventoPartido e : partido.getEventos()) {
            if (e instanceof Gol gol) {
                EstadisticasJugador stats = obtenerStats(statsMap, gol.getAutor(), gol.getEquipo());
                stats.incrementarGoles();
            } else if (e instanceof Tarjeta tarjeta) {
                EstadisticasJugador stats = obtenerStats(statsMap, tarjeta.getJugador(), tarjeta.getEquipo());
                stats.incrementarTarjetas();
            } else if (e instanceof Lesion lesion) {
                EstadisticasJugador stats = obtenerStats(statsMap, lesion.getJugador(), lesion.getEquipo());
                stats.setLesionado(true);
            } else if (e instanceof Cambio cambio) {
                obtenerStats(statsMap, cambio.getSale(), cambio.getEquipo());
                obtenerStats(statsMap, cambio.getEntra(), cambio.getEquipo());
            }
        }

        calcularMinutosJugados(statsMap, partido);
        actualizarRendimientosFinales(statsMap, partido.getLocal());
        actualizarRendimientosFinales(statsMap, partido.getVisitante());

        return new ArrayList<>(statsMap.values());
    }

    private void actualizarRendimientosFinales(
            Map<Integer, EstadisticasJugador> statsMap,
            Equipo equipo) {
        for (IJugador jugador : equipo.getTitulares()) {
            actualizarRendimientoFinal(statsMap, jugador);
        }
        for (IJugador jugador : equipo.getSuplentes()) {
            actualizarRendimientoFinal(statsMap, jugador);
        }
    }

    private void actualizarRendimientoFinal(
            Map<Integer, EstadisticasJugador> statsMap,
            IJugador jugador) {
        EstadisticasJugador stats = statsMap.get(jugador.getId());
        if (stats != null) {
            stats.setRendimientoFinal(jugador.getRendimiento());
        }
    }

    private void calcularMinutosJugados(
            Map<Integer, EstadisticasJugador> statsMap,
            Partido partido) {
        List<Cambio> cambios = partido.getEventos().stream()
                .filter(Cambio.class::isInstance)
                .map(Cambio.class::cast)
                .toList();

        Set<Integer> jugadoresEnCampo = new HashSet<>();
        partido.getLocal().getTitulares().forEach(j -> jugadoresEnCampo.add(j.getId()));
        partido.getVisitante().getTitulares().forEach(j -> jugadoresEnCampo.add(j.getId()));

        for (int i = cambios.size() - 1; i >= 0; i--) {
            Cambio cambio = cambios.get(i);
            jugadoresEnCampo.remove(cambio.getEntra().getId());
            jugadoresEnCampo.add(cambio.getSale().getId());
        }

        Map<Integer, Integer> ingresoAlCampo = new HashMap<>();
        Map<Integer, Integer> minutosAcumulados = new HashMap<>();
        jugadoresEnCampo.forEach(id -> ingresoAlCampo.put(id, 0));

        for (Cambio cambio : cambios) {
            int idSale = cambio.getSale().getId();
            int idEntra = cambio.getEntra().getId();
            int inicio = ingresoAlCampo.getOrDefault(idSale, cambio.getMinuto());
            minutosAcumulados.merge(idSale, Math.max(0, cambio.getMinuto() - inicio), Integer::sum);
            ingresoAlCampo.remove(idSale);
            ingresoAlCampo.put(idEntra, cambio.getMinuto());
        }

        for (Map.Entry<Integer, Integer> jugadorActivo : ingresoAlCampo.entrySet()) {
            minutosAcumulados.merge(
                    jugadorActivo.getKey(),
                    Math.max(0, partido.getMinuto() - jugadorActivo.getValue()),
                    Integer::sum);
        }

        statsMap.forEach((idJugador, stats) ->
                stats.setMinutosJugados(minutosAcumulados.getOrDefault(idJugador, 0)));
    }

    private void registrarPlantelCompleto(Map<Integer, EstadisticasJugador> statsMap, Equipo equipo) {
        for (IJugador jugador : equipo.getTitulares()) {
            obtenerStats(statsMap, jugador, equipo);
        }
        for (IJugador jugador : equipo.getSuplentes()) {
            obtenerStats(statsMap, jugador, equipo);
        }
    }

    private EstadisticasJugador obtenerStats(
            Map<Integer, EstadisticasJugador> statsMap,
            IJugador jugador,
            Equipo equipo) {
        EstadisticasJugador stats = statsMap.computeIfAbsent(
                jugador.getId(),
                id -> new EstadisticasJugador(jugador, equipo.getId()));
        stats.setRendimientoFinal(jugador.getRendimiento());
        return stats;
    }
}
