package simulador.torneo.domain;

public class Jugador implements IJugador {
    private String nombre;
    private String posicion;
    private int rendimientoBase;

    public Jugador(String nombre, String posicion, int rendimientoBase) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.rendimientoBase = rendimientoBase;
    }

    @Override
    public String getNombre() { return nombre; }

    @Override
    public int getRendimiento() { return rendimientoBase; }
}
