package simulador.state;

import simulador.composite.Partido;

/**
 * [PATRON: State — ConcreteState]
 *
 * Que hace: Representa el estado del partido durante los primeros 45 minutos.
 * permiteEventosJuego() retorna true (el motor genera eventos).
 * permiteCambios() retorna false (el DT no puede hacer cambios aun).
 * avanzar() transiciona a Entretiempo cuando se llega al minuto 45.
 * iniciar() lanza UnsupportedOperationException (el partido ya comenzo).
 *
 * Relaciones:
 * - Implementa: EstadoPartido
 * - Composicion con: (ninguna)
 * - Asociacion con: Partido (recibido en avanzar() para ejecutar la transicion)
 * - Usada por (dependencia): NoIniciado (la crea en iniciar()),
 *   MotorSimulacion (consulta permiteEventosJuego() en cada minuto)
 * - Crea (Creator GRASP): Entretiempo (en avanzar(), realiza la transicion)
 *
 * GRASP:
 * - Polimorfismo: cumple porque encapsula el comportamiento especifico del primer
 *   tiempo sin necesidad de condicionales en el MotorSimulacion ni en Partido.
 */
public class PrimerTiempo implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha comenzado");
    }

    @Override
    public void avanzar(Partido p) {
        p.setEstado(new Entretiempo());
    }

    @Override
    public boolean permiteCambios() {
        return false;
    }

    @Override
    public boolean permiteEventosJuego() {
        return true;
    }

    @Override
    public String getNombre() {
        return "Primer Tiempo";
    }
}
