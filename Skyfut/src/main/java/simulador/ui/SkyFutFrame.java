package simulador.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import simulador.composite.Partido;
import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.FixturePartido;
import simulador.dto.Goleador;
import simulador.dto.ResumenTorneo;
import simulador.events.EventoPartido;
import simulador.facade.TorneoFacade;
import simulador.strategy.TacticaDefensiva;
import simulador.strategy.TacticaEquilibrada;
import simulador.strategy.TacticaOfensiva;
import simulador.strategy.TacticaStrategy;

public class SkyFutFrame extends JFrame {
    private final TorneoFacade facade;

    private final JComboBox<Equipo> equipoCombo;
    private final JComboBox<IJugador> saleCombo;
    private final JComboBox<IJugador> entraCombo;
    private final JComboBox<TacticaOption> tacticaCombo;

    private final JButton seleccionarEquipoButton;
    private final JButton iniciarTorneoButton;
    private final JButton simularSiguienteButton;
    private final JButton confirmarCambioButton;
    private final JButton aplicarTacticaButton;
    private final JButton simularSegundoTiempoButton;
    private final JButton verFixtureButton;
    private final JButton refrescarResultadosButton;

    private final JLabel equipoDtLabel;
    private final JLabel estadoLabel;
    private final JLabel partidoLabel;

    private final JTextArea eventosArea;
    private final JTextArea resultadosArea;
    private final JTextArea plantelArea;
    private boolean torneoCompleto;
    private boolean campeonAvisado;

    public SkyFutFrame() {
        this.facade = new TorneoFacade();
        this.equipoCombo = new JComboBox<>();
        this.saleCombo = new JComboBox<>();
        this.entraCombo = new JComboBox<>();
        this.tacticaCombo = new JComboBox<>();
        this.seleccionarEquipoButton = new JButton("Elegir DT");
        this.iniciarTorneoButton = new JButton("Iniciar torneo");
        this.simularSiguienteButton = new JButton("Simular siguiente partido");
        this.confirmarCambioButton = new JButton("Confirmar cambio");
        this.aplicarTacticaButton = new JButton("Aplicar tactica");
        this.simularSegundoTiempoButton = new JButton("Simular segundo tiempo");
        this.verFixtureButton = new JButton("Ver fixture");
        this.refrescarResultadosButton = new JButton("Refrescar resultados");
        this.equipoDtLabel = new JLabel("DT sin equipo");
        this.estadoLabel = new JLabel("Elegir un equipo para comenzar");
        this.partidoLabel = new JLabel("Sin partido actual");
        this.eventosArea = crearAreaTexto();
        this.resultadosArea = crearAreaTexto();
        this.plantelArea = crearAreaTexto();

        configurarVentana();
        configurarRenderers();
        configurarAcciones();
        cargarEquipos();
        cargarTacticas();
        actualizarEstadoControles();
    }

    private void configurarVentana() {
        setTitle("SkyFut - Simulador de Torneo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1020, 680));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        root.add(crearPanelSuperior(), BorderLayout.NORTH);
        root.add(crearPanelCentral(), BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Torneo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Equipo del DT:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(equipoCombo, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(seleccionarEquipoButton, gbc);

        gbc.gridx = 3;
        panel.add(iniciarTorneoButton, gbc);

        gbc.gridx = 4;
        panel.add(simularSiguienteButton, gbc);

        gbc.gridx = 5;
        panel.add(verFixtureButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Seleccionado:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(equipoDtLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 3;
        estadoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(estadoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        partidoLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        panel.add(partidoLabel, gbc);

        return panel;
    }

    private JSplitPane crearPanelCentral() {
        JPanel izquierda = new JPanel(new BorderLayout(8, 8));
        izquierda.add(crearPanelEntretiempo(), BorderLayout.NORTH);

        JTabbedPane tabsIzquierda = new JTabbedPane();
        tabsIzquierda.addTab("Eventos", new JScrollPane(eventosArea));
        tabsIzquierda.addTab("Plantel DT", new JScrollPane(plantelArea));
        izquierda.add(tabsIzquierda, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new BorderLayout(8, 8));
        derecha.setBorder(BorderFactory.createTitledBorder("Resumen"));
        derecha.add(new JScrollPane(resultadosArea), BorderLayout.CENTER);
        derecha.add(refrescarResultadosButton, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, derecha);
        split.setResizeWeight(0.58);
        return split;
    }

    private JPanel crearPanelEntretiempo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Entretiempo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Sale:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(saleCombo, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Entra:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        panel.add(entraCombo, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(confirmarCambioButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Tactica:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(tacticaCombo, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        panel.add(aplicarTacticaButton, gbc);

        gbc.gridx = 4;
        panel.add(simularSegundoTiempoButton, gbc);

        return panel;
    }

    private JTextArea crearAreaTexto() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private void configurarRenderers() {
        equipoCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Equipo equipo) {
                    setText(equipo.getNombre());
                }
                return this;
            }
        });

        DefaultListCellRenderer jugadorRenderer = new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof IJugador jugador) {
                    setText(jugador.getNombre() + " - " + jugador.getPosicion()
                            + " (" + String.format("%.2f", jugador.getRendimiento()) + ")");
                }
                return this;
            }
        };
        saleCombo.setRenderer(jugadorRenderer);
        entraCombo.setRenderer(jugadorRenderer);
    }

    private void configurarAcciones() {
        seleccionarEquipoButton.addActionListener(e -> ejecutarSeguro(this::seleccionarEquipo));
        iniciarTorneoButton.addActionListener(e -> ejecutarSeguro(this::iniciarTorneo));
        simularSiguienteButton.addActionListener(e -> ejecutarSeguro(this::simularSiguientePartido));
        confirmarCambioButton.addActionListener(e -> ejecutarSeguro(this::confirmarCambio));
        aplicarTacticaButton.addActionListener(e -> ejecutarSeguro(this::aplicarTactica));
        simularSegundoTiempoButton.addActionListener(e -> ejecutarSeguro(this::simularSegundoTiempo));
        verFixtureButton.addActionListener(e -> ejecutarSeguro(this::mostrarFixture));
        refrescarResultadosButton.addActionListener(e -> ejecutarSeguro(this::actualizarResultados));
    }

    private void cargarEquipos() {
        DefaultComboBoxModel<Equipo> model = new DefaultComboBoxModel<>();
        for (Equipo equipo : facade.cargarEquipos()) {
            model.addElement(equipo);
        }
        equipoCombo.setModel(model);
    }

    private void cargarTacticas() {
        DefaultComboBoxModel<TacticaOption> model = new DefaultComboBoxModel<>();
        model.addElement(new TacticaOption("Equilibrada", new TacticaEquilibrada()));
        model.addElement(new TacticaOption("Ofensiva", new TacticaOfensiva()));
        model.addElement(new TacticaOption("Defensiva", new TacticaDefensiva()));
        tacticaCombo.setModel(model);
    }

    private void seleccionarEquipo() {
        Equipo elegido = (Equipo) equipoCombo.getSelectedItem();
        if (elegido == null) {
            mostrarInfo("No hay equipos disponibles para seleccionar.");
            return;
        }
        Equipo equipoDt = facade.seleccionarEquipo(elegido.getId());
        equipoDtLabel.setText(equipoDt.getNombre());
        estadoLabel.setText("Equipo del DT confirmado");
        actualizarPlantel();
        actualizarEstadoControles();
    }

    private void iniciarTorneo() {
        if (facade.getEquipoDt() == null) {
            seleccionarEquipo();
        }
        facade.iniciarTorneo();
        torneoCompleto = false;
        campeonAvisado = false;
        estadoLabel.setText("Torneo iniciado");
        eventosArea.setText("Torneo generado. Simula el siguiente partido para comenzar.\n");
        actualizarResultados();
        actualizarEstadoControles();
    }

    private void simularSiguientePartido() {
        facade.simularSiguientePartido();
        Partido partido = facade.getPartidoActual();
        if (facade.partidoActualEsDelDT() && facade.partidoActualEstaEnEntretiempo()) {
            estadoLabel.setText("Entretiempo: podes hacer cambios y cambiar tactica");
        } else {
            estadoLabel.setText("Partido simulado");
        }
        actualizarVistaPartido();
        actualizarResultados();
        actualizarEstadoControles();
    }

    private void confirmarCambio() {
        Equipo equipoDt = obtenerEquipoDtEnPartido();
        IJugador sale = (IJugador) saleCombo.getSelectedItem();
        IJugador entra = (IJugador) entraCombo.getSelectedItem();
        if (equipoDt == null || sale == null || entra == null) {
            mostrarInfo("Selecciona un titular y un suplente para realizar el cambio.");
            return;
        }
        facade.realizarCambio(equipoDt, sale, entra);
        estadoLabel.setText("Cambio confirmado");
        actualizarVistaPartido();
        actualizarEstadoControles();
    }

    private void aplicarTactica() {
        Equipo equipoDt = obtenerEquipoDtEnPartido();
        TacticaOption option = (TacticaOption) tacticaCombo.getSelectedItem();
        if (equipoDt == null || option == null) {
            mostrarInfo("Selecciona una tactica para aplicar.");
            return;
        }
        facade.cambiarTactica(equipoDt, option.tactica());
        estadoLabel.setText("Tactica aplicada: " + option.nombre());
        actualizarPlantel();
    }

    private void simularSegundoTiempo() {
        facade.simularSegundoTiempoPartidoActual();
        Partido partido = facade.getPartidoActual();
        if (partido != null && partido.getGanador() != null) {
            estadoLabel.setText("Finalizado. Ganador: " + partido.getGanador().getNombre());
        } else {
            estadoLabel.setText("Segundo tiempo simulado");
        }
        actualizarVistaPartido();
        actualizarResultados();
        actualizarEstadoControles();
    }

    private void actualizarVistaPartido() {
        actualizarPartidoLabel();
        actualizarEventos();
        actualizarPlantel();
    }

    private void actualizarPartidoLabel() {
        Partido partido = facade.getPartidoActual();
        if (partido == null) {
            partidoLabel.setText("Sin partido actual");
            return;
        }

        String ganador = partido.getGanador() == null ? "" : " | Ganador: " + partido.getGanador().getNombre();
        partidoLabel.setText(
                partido.getLocal().getNombre() + " " + partido.getGolesLocal()
                        + " - " + partido.getGolesVisitante() + " " + partido.getVisitante().getNombre()
                        + " | Minuto " + partido.getMinuto()
                        + " | Estado: " + partido.getEstado().getNombre()
                        + ganador);
    }

    private void actualizarEventos() {
        Partido partido = facade.getPartidoActual();
        if (partido == null) {
            eventosArea.setText("Sin eventos para mostrar.\n");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(partido.getLocal().getNombre()).append(" vs ")
                .append(partido.getVisitante().getNombre()).append("\n\n");
        List<EventoPartido> eventos = partido.getEventos();
        if (eventos.isEmpty()) {
            sb.append("Sin eventos registrados en este tramo.\n");
        } else {
            for (EventoPartido evento : eventos) {
                sb.append("Min ").append(evento.getMinuto())
                        .append(" | ").append(evento.getTipo())
                        .append(" | ").append(evento.getDescripcion())
                        .append("\n");
            }
        }
        eventosArea.setText(sb.toString());
        eventosArea.setCaretPosition(0);
    }

    private void actualizarPlantel() {
        Equipo equipo = obtenerEquipoDtEnPartido();
        if (equipo == null) {
            equipo = facade.getEquipoDt();
        }

        if (equipo == null) {
            plantelArea.setText("Todavia no hay equipo del DT.\n");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(equipo.getNombre()).append("\n");
        if (equipo.getTactica() != null) {
            sb.append("Tactica: ").append(equipo.getTactica().getFormacion()).append("\n");
        }
        sb.append("Rendimiento total: ").append(String.format("%.2f", equipo.getRendimientoTotal())).append("\n\n");
        sb.append("Titulares\n");
        for (IJugador jugador : equipo.getTitulares()) {
            sb.append("- ").append(formatearJugador(jugador)).append("\n");
        }
        sb.append("\nSuplentes\n");
        for (IJugador jugador : equipo.getSuplentes()) {
            sb.append("- ").append(formatearJugador(jugador)).append("\n");
        }
        plantelArea.setText(sb.toString());
        plantelArea.setCaretPosition(0);
    }

    private void actualizarResultados() {
        if (!facade.torneoIniciado()) {
            resultadosArea.setText("Inicia el torneo para ver resultados.\n");
            return;
        }

        ResumenTorneo resumen = facade.consultarResultados();
        List<FixturePartido> fixture = facade.consultarFixture();
        torneoCompleto = resumen.isCompleto();
        StringBuilder sb = new StringBuilder();
        sb.append(resumen.getNombreTorneo()).append("\n");
        sb.append("Estado: ").append(resumen.isCompleto() ? "FINALIZADO" : "EN CURSO").append("\n\n");
        sb.append("Resultados\n");
        if (fixture.isEmpty()) {
            sb.append("- Sin partidos finalizados todavia.\n");
        } else {
            for (Map.Entry<String, List<FixturePartido>> fase : agruparFixturePorFase(fixture).entrySet()) {
                sb.append("\n").append(fase.getKey()).append("\n");
                for (FixturePartido partido : fase.getValue()) {
                    sb.append("- ")
                            .append(partido.equipoLocal()).append(" ");
                    if (partido.finalizado()) {
                        sb.append(partido.golesLocal()).append(" - ")
                                .append(partido.golesVisitante()).append(" ");
                    } else {
                        sb.append("vs ");
                    }
                    sb.append(partido.equipoVisitante())
                            .append(" | ").append(partido.estado());
                    if (partido.ganador() != null) {
                        sb.append(" | Ganador: ").append(partido.ganador());
                    }
                    sb.append("\n");
                }
            }
        }

        sb.append("\nGoleadores\n");
        if (resumen.getGoleadores().isEmpty()) {
            sb.append("- Sin goles registrados.\n");
        } else {
            for (Goleador goleador : resumen.getGoleadores()) {
                sb.append("- ").append(goleador.getNombreJugador())
                        .append(" (").append(goleador.getNombreEquipo()).append("): ")
                        .append(goleador.getGoles()).append("\n");
            }
        }
        resultadosArea.setText(sb.toString());
        resultadosArea.setCaretPosition(0);
        actualizarEstadoControles();
        avisarCampeonSiCorresponde();
    }

    private void actualizarEstadoControles() {
        boolean torneoIniciado = facade.torneoIniciado();
        boolean hayEquipoDt = facade.getEquipoDt() != null;
        boolean entretiempoDt = facade.partidoActualEsDelDT() && facade.partidoActualEstaEnEntretiempo();

        equipoCombo.setEnabled(!torneoIniciado);
        seleccionarEquipoButton.setEnabled(!torneoIniciado);
        iniciarTorneoButton.setEnabled(hayEquipoDt && !torneoIniciado);
        simularSiguienteButton.setEnabled(torneoIniciado && !entretiempoDt && !torneoCompleto);
        verFixtureButton.setEnabled(torneoIniciado);

        saleCombo.setEnabled(entretiempoDt);
        entraCombo.setEnabled(entretiempoDt);
        tacticaCombo.setEnabled(entretiempoDt);
        confirmarCambioButton.setEnabled(entretiempoDt);
        aplicarTacticaButton.setEnabled(entretiempoDt);
        simularSegundoTiempoButton.setEnabled(entretiempoDt);

        cargarJugadoresParaCambio();
    }

    private Map<String, List<FixturePartido>> agruparFixturePorFase(List<FixturePartido> fixture) {
        Map<String, List<FixturePartido>> porFase = new LinkedHashMap<>();
        for (FixturePartido partido : fixture) {
            porFase.computeIfAbsent(partido.faseNombre(), key -> new ArrayList<>()).add(partido);
        }
        return porFase;
    }

    private void mostrarFixture() {
        if (!facade.torneoIniciado()) {
            mostrarInfo("Inicia el torneo para ver el fixture.");
            return;
        }

        JDialog dialog = new JDialog(this, "Fixture del torneo", false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(new JScrollPane(new FixturePanel(facade.consultarFixture())), BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void avisarCampeonSiCorresponde() {
        if (!torneoCompleto || campeonAvisado) {
            return;
        }

        String campeon = facade.consultarCampeon();
        if (campeon == null) {
            return;
        }

        campeonAvisado = true;
        mostrarInfo("Torneo finalizado. Campeon: " + campeon);
    }

    private void cargarJugadoresParaCambio() {
        Equipo equipo = obtenerEquipoDtEnPartido();
        DefaultComboBoxModel<IJugador> titularesModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<IJugador> suplentesModel = new DefaultComboBoxModel<>();
        if (equipo != null) {
            for (IJugador jugador : equipo.getTitulares()) {
                titularesModel.addElement(jugador);
            }
            for (IJugador jugador : equipo.getSuplentes()) {
                suplentesModel.addElement(jugador);
            }
        }
        saleCombo.setModel(titularesModel);
        entraCombo.setModel(suplentesModel);
    }

    private Equipo obtenerEquipoDtEnPartido() {
        Partido partido = facade.getPartidoActual();
        Equipo equipoDt = facade.getEquipoDt();
        if (partido == null || equipoDt == null) {
            return null;
        }
        if (partido.getLocal().getId() == equipoDt.getId()) {
            return partido.getLocal();
        }
        if (partido.getVisitante().getId() == equipoDt.getId()) {
            return partido.getVisitante();
        }
        return null;
    }

    private String formatearJugador(IJugador jugador) {
        return jugador.getNombre() + " - " + jugador.getPosicion()
                + " | rendimiento " + String.format("%.2f", jugador.getRendimiento());
    }

    private void ejecutarSeguro(Runnable accion) {
        try {
            accion.run();
        } catch (Exception ex) {
            mostrarError(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
            actualizarEstadoControles();
        }
    }

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "SkyFut", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private record TacticaOption(String nombre, TacticaStrategy tactica) {
        @Override
        public String toString() {
            return nombre + " (" + tactica.getFormacion() + ")";
        }
    }
}
