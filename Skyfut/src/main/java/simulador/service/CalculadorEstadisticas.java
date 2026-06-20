package simulador.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        registrarPlantelCompleto(statsMap, partido.getLocal(), partido.getMinuto());
        registrarPlantelCompleto(statsMap, partido.getVisitante(), partido.getMinuto());

        for (EventoPartido e : partido.getEventos()) {
            if (e instanceof Gol gol) {
                EstadisticasJugador stats = obtenerStats(statsMap, gol.getAutor(), gol.getEquipo(), partido.getMinuto());
                stats.incrementarGoles();
            } else if (e instanceof Tarjeta tarjeta) {
                EstadisticasJugador stats = obtenerStats(statsMap, tarjeta.getJugador(), tarjeta.getEquipo(), partido.getMinuto());
                stats.incrementarTarjetas();
            } else if (e instanceof Lesion lesion) {
                EstadisticasJugador stats = obtenerStats(statsMap, lesion.getJugador(), lesion.getEquipo(), partido.getMinuto());
                stats.setLesionado(true);
            } else if (e instanceof Cambio cambio) {
                obtenerStats(statsMap, cambio.getSale(), cambio.getEquipo(), cambio.getMinuto())
                        .setMinutosJugados(cambio.getMinuto());
                obtenerStats(statsMap, cambio.getEntra(), cambio.getEquipo(), partido.getMinuto());
            }
        }

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

    private void registrarPlantelCompleto(Map<Integer, EstadisticasJugador> statsMap, Equipo equipo, int minutos) {
        for (IJugador jugador : equipo.getTitulares()) {
            obtenerStats(statsMap, jugador, equipo, minutos);
        }
        for (IJugador jugador : equipo.getSuplentes()) {
            obtenerStats(statsMap, jugador, equipo, 0);
        }
    }

    private EstadisticasJugador obtenerStats(
            Map<Integer, EstadisticasJugador> statsMap,
            IJugador jugador,
            Equipo equipo,
            int minutosJugados) {
        EstadisticasJugador stats = statsMap.computeIfAbsent(
                jugador.getId(),
                id -> new EstadisticasJugador(jugador, equipo.getId()));
        stats.setMinutosJugados(Math.max(stats.getMinutosJugados(), minutosJugados));
        stats.setRendimientoFinal(jugador.getRendimiento());
        return stats;
    }
}
