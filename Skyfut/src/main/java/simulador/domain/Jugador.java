package simulador.domain;

/**
 * [PATRON: Decorator — ConcreteComponent]
 *
 * Que hace: Representa a un jugador real de futbol con sus datos basicos
 * (nombre, posicion, rendimientoBase). Es el componente concreto que los
 * decorators envuelven para modificar su rendimiento en tiempo de ejecucion.
 * Su getRendimiento() devuelve directamente el rendimientoBase sin modificaciones.
 *
 * Relaciones:
 * - Implementa: IJugador
 * - Composicion con: (ninguna — clase simple de datos)
 * - Asociacion con: (ninguna)
 * - Usada por (dependencia): RepositorioJugador (la crea), JugadorDecorator (la envuelve),
 *   CalculadorEstadisticas
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Alta Cohesion: cumple porque solo encapsula los datos inmutables de un jugador
 *   (nombre, posicion, rendimiento base) sin mezclar responsabilidades.
 * - Bajo Acoplamiento: cumple porque no depende de ninguna otra clase del dominio,
 *   solo implementa la interfaz IJugador.
 */
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
