package simulador.state;

import simulador.composite.Partido;

/**
 * [PATRON: State — ConcreteState]
 *
 * Que hace: Representa el estado del partido durante los ultimos 45 minutos (46-90).
 * permiteEventosJuego() retorna true (el motor sigue generando eventos).
 * permiteCambios() retorna false (el entretiempo ya paso).
 * avanzar() transiciona a Finalizado cuando se llega al minuto 90.
 * iniciar() lanza UnsupportedOperationException.
 *
 * Relaciones:
 * - Implementa: EstadoPartido
 * - Composicion con: (ninguna)
 * - Asociacion con: Partido (recibido en avanzar() para ejecutar la transicion)
 * - Usada por (dependencia): Entretiempo (la crea en avanzar()),
 *   MotorSimulacion (consulta permiteEventosJuego() en cada minuto)
 * - Crea (Creator GRASP): Finalizado (en avanzar(), realiza la transicion)
 *
 * GRASP:
 * - Polimorfismo: cumple porque encapsula el comportamiento especifico del segundo
 *   tiempo de forma simetrica al PrimerTiempo, sin duplicar logica en el motor.
 */
public class SegundoTiempo implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha comenzado");
    }

    @Override
    public void avanzar(Partido p) {
        p.setEstado(new Finalizado());
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
        return "Segundo Tiempo";
    }
}
