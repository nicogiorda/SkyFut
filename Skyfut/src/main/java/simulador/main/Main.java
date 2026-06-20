package simulador.main;

import javax.swing.SwingUtilities;

import simulador.ui.SkyFutFrame;

/**
 * [PATRON: (ninguno) — Punto de entrada de la aplicacion]
 *
 * Que hace: Clase principal de la aplicacion. Lanza SkyFutFrame en el Event
 * Dispatch Thread (EDT) de Swing usando SwingUtilities.invokeLater(), garantizando
 * que toda la inicializacion de la UI ocurra en el hilo correcto.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna)
 * - Asociacion con: (ninguna)
 * - Usada por (dependencia): SkyFutFrame (la instancia y la hace visible)
 * - Crea (Creator GRASP): SkyFutFrame (dentro del lambda de invokeLater)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque solo conoce a SkyFutFrame; no tiene
 *   referencias directas a TorneoFacade, servicios ni repositorios.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SkyFutFrame().setVisible(true));
    }
}
