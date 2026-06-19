package simulador.dto;

public record EstadisticaJugadorTorneo(
        String jugador,
        String posicion,
        int partidos,
        int goles,
        int asistencias,
        int tarjetasAmarillas,
        int lesiones,
        int minutosJugados,
        double rendimientoPromedio) {
}
