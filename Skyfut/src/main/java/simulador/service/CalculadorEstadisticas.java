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

        return new ArrayList<>(statsMap.values());
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
