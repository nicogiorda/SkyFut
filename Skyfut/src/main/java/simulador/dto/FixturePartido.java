package simulador.dto;

public record FixturePartido(
        int faseOrden,
        String faseNombre,
        int partidoId,
        int ordenPartido,
        String equipoLocal,
        String equipoVisitante,
        int golesLocal,
        int golesVisitante,
        String ganador,
        String estado) {

    public boolean finalizado() {
        return "FINALIZADO".equals(estado);
    }
}
