package simulador.state;

import simulador.composite.Partido;

/**
 * [PATRON: State — ConcreteState]
 *
 * Que hace: Representa el estado inicial del partido, antes de que comience.
 * iniciar() es la unica transicion valida: cambia el estado del Partido a PrimerTiempo.
 * avanzar() lanza UnsupportedOperationException porque no tiene sentido avanzar
 * un partido que no ha comenzado. No permite cambios ni eventos de juego.
 *
 * Relaciones:
 * - Implementa: EstadoPartido
 * - Composicion con: (ninguna)
 * - Asociacion con: Partido (recibido en iniciar/avanzar para ejecutar la transicion)
 * - Usada por (dependencia): Partido (lo inicializa con new NoIniciado() en el constructor)
 * - Crea (Creator GRASP): PrimerTiempo (en iniciar(), realiza la transicion)
 *
 * GRASP:
 * - Polimorfismo: cumple porque implementa el comportamiento especifico del estado
 *   no iniciado sin que Partido necesite un condicional para este caso.
 */
public class NoIniciado implements EstadoPartido {
    @Override
    public void iniciar(Partido p) {
        p.setEstado(new PrimerTiempo());
    }

    @Override
    public void avanzar(Partido p) {
        throw new UnsupportedOperationException("El partido no ha comenzado");
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
        return "No Iniciado";
    }
}
