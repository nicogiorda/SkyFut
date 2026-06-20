package simulador.state;

import simulador.composite.Partido;

/**
 * [PATRON: State — ConcreteState]
 *
 * Que hace: Representa el estado terminal del partido, una vez concluidos los 90
 * minutos. Tanto iniciar() como avanzar() lanzan UnsupportedOperationException
 * porque no hay transiciones posibles desde este estado. Ni cambios ni eventos
 * de juego estan permitidos. Partido.estaCompleto() se implementa verificando
 * si el estado actual es instanceof Finalizado.
 *
 * Relaciones:
 * - Implementa: EstadoPartido
 * - Composicion con: (ninguna)
 * - Asociacion con: Partido (recibido en iniciar/avanzar, pero ambos lanzan excepcion)
 * - Usada por (dependencia): SegundoTiempo (la crea en avanzar()),
 *   Partido.estaCompleto() (verifica instanceof Finalizado),
 *   MotorSimulacion (usa estaCompleto() como condicion de salida del loop)
 * - Crea (Creator GRASP): (no aplica — estado terminal sin transiciones)
 *
 * GRASP:
 * - Polimorfismo: cumple porque encapsula el comportamiento del estado finalizado
 *   (todo prohibido) sin necesitar condicionales en el motor ni en la facade.
 */
public class Finalizado implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha finalizado");
    }

    @Override
    public void avanzar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha finalizado");
    }

    @Override
    public boolean permiteCambios() {
        return false;
    }

    @Override
    public boolean permiteEventosJuego() {
        return false;
    }

    @Override
    public String getNombre() {
        return "Finalizado";
    }
}
