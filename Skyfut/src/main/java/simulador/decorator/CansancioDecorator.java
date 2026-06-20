package simulador.decorator;

import simulador.domain.IJugador;

/**
 * [PATRON: Decorator — ConcreteDecorator]
 *
 * Que hace: Reduce el rendimiento de un jugador de forma progresiva segun los
 * minutos jugados. Formula: max(0.5, 1.0 - minutoJugado/300.0). Con 45 minutos
 * jugados el modificador es 0.85; con 90 minutos es 0.70 (minimo 0.50).
 * Es aplicado por MotorSimulacion al finalizar cada tiempo para todos los titulares.
 *
 * Relaciones:
 * - Hereda de: JugadorDecorator
 * - Composicion con: (ninguna adicional a la del padre)
 * - Asociacion con: (ninguna)
 * - Usada por (dependencia): MotorSimulacion.aplicarCansancio() (la instancia via lambda
 *   j -> new CansancioDecorator(j, minuto) y la aplica a cada titular con decorarTitular())
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - OCP (Open/Closed Principle): cumple porque extiende el comportamiento de Jugador
 *   sin modificar su codigo. El cansancio es una responsabilidad adicional que se
 *   agrega en tiempo de ejecucion mediante composicion.
 */
public class CansancioDecorator extends JugadorDecorator {
    private int minutoJugado;

    public CansancioDecorator(IJugador jugador, int minutoJugado) {
        super(jugador);
        this.minutoJugado = minutoJugado;
    }

    @Override
    public double getRendimiento() {
        return jugador.getRendimiento() * getModificador();
    }

    @Override
    public String getNombreDecorador() {
        return "Cansancio";
    }

    @Override
    public String getImpactoDecorador() {
        return "x" + String.format("%.2f", getModificador());
    }

    private double getModificador() {
        return Math.max(0.5, 1.0 - (minutoJugado / 300.0));
    }
}
