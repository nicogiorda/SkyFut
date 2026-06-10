package simulador.domain;

public class Jugador implements IJugador {
    private String nombre;
    private String posicion;
    private double rendimientoBase;

    public Jugador(String nombre, String posicion, double rendimientoBase) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.rendimientoBase = rendimientoBase;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getPosicion() {
        return posicion;
    }

    @Override
    public double getRendimiento() {
        return rendimientoBase;
    }
}
