package simulador.state;

import simulador.composite.Partido;

/**
 * [PATRON: State — ConcreteState]
 *
 * Que hace: Representa el descanso entre los dos tiempos. Es el UNICO estado
 * donde permiteCambios() retorna true, habilitando al DT para realizar sustituciones
 * y cambiar la tactica. permiteEventosJuego() retorna false (no hay juego activo).
 * avanzar() transiciona a SegundoTiempo cuando el DT confirma continuar.
 *
 * Relaciones:
 * - Implementa: EstadoPartido
 * - Composicion con: (ninguna)
 * - Asociacion con: Partido (recibido en avanzar() para ejecutar la transicion)
 * - Usada por (dependencia): PrimerTiempo (la crea en avanzar()),
 *   TorneoFacade y GestorPartido (consultan permiteCambios() para validar acciones del DT),
 *   MotorSimulacion (aplica cambios automaticos al rival en este estado)
 * - Crea (Creator GRASP): SegundoTiempo (en avanzar(), realiza la transicion)
 *
 * GRASP:
 * - Polimorfismo: cumple porque es el unico estado que retorna true en permiteCambios(),
 *   sin necesitar un condicional if(estado == entretiempo) en TorneoFacade.
 */
public class Entretiempo implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        throw new UnsupportedOperationException("El partido ya ha comenzado");
    }

    @Override
    public void avanzar(Partido p) {
        p.setEstado(new SegundoTiempo());
    }

    @Override
    public boolean permiteCambios() {
        return true;
    }

    @Override
    public boolean permiteEventosJuego() {
        return false;
    }

    @Override
    public String getNombre() {
        return "Entretiempo";
    }
}
