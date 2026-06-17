package simulador.main;

import javax.swing.SwingUtilities;

import simulador.ui.SkyFutFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SkyFutFrame().setVisible(true));
    }
}
