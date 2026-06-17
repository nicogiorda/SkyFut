package simulador.domain;

public class Jugador implements IJugador {
    private int id;
    private String nombre;
    private String posicion;
    private double rendimientoBase;

    public Jugador(String nombre, String posicion, double rendimientoBase) {
        this(0, nombre, posicion, rendimientoBase);
    }

    public Jugador(int id, String nombre, String posicion, double rendimientoBase) {
        this.id = id;
        this.nombre = nombre;
        this.posicion = posicion;
        this.rendimientoBase = rendimientoBase;
    }

    public int getId() {
        return id;
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
