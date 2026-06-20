package simulador.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;

import simulador.composite.Partido;
import simulador.decorator.JugadorDecorator;
import simulador.domain.Equipo;
import simulador.domain.IJugador;
import simulador.dto.EstadisticaJugadorTorneo;
import simulador.dto.FixturePartido;
import simulador.dto.Goleador;
import simulador.dto.ResumenTorneo;
import simulador.events.EventoPartido;
import simulador.facade.TorneoFacade;
import simulador.strategy.TacticaDefensiva;
import simulador.strategy.TacticaEquilibrada;
import simulador.strategy.TacticaOfensiva;
import simulador.strategy.TacticaStrategy;

/**
 * [PATRON: (ninguno) — Vista principal Swing]
 *
 * Que hace: Es la ventana principal de la aplicacion. Construye y gestiona toda la
 * interfaz grafica: seleccion de equipo DT, pestanas de juego (Partido, Fixture,
 * Resultados, Estadisticas), controles del entretiempo (cambios y tactica), log de
 * eventos y tabla de estadisticas. Se comunica EXCLUSIVAMENTE con TorneoFacade,
 * respetando el patron Facade. Contiene clases internas para los componentes
 * graficos custom: BackgroundPanel, RoundedPanel, DecoratedContent, SkyButton, TabButton,
 * SpeedMarks.
 *
 * Relaciones:
 * - Hereda de: JFrame (Swing)
 * - Composicion con: multiples controles Swing (JComboBox, JTextArea, JTable, JPanel, etc.)
 * - Asociacion con: TorneoFacade torneoFacade (unica referencia al subsistema de negocio;
 *   todos los eventos de usuario se delegan a ella)
 * - Usada por (dependencia): Main (la instancia y la hace visible en el EDT),
 *   FixturePanel (usado en un JDialog para mostrar el cuadro del torneo)
 * - Crea (Creator GRASP): TorneoFacade (en el constructor), FixturePanel (al mostrar fixture)
 *
 * GRASP:
 * - Controller (GRASP Controller): cumple porque recibe los gestos del usuario
 *   (clicks, selecciones) y los delega a TorneoFacade; no contiene logica de negocio.
 * - Bajo Acoplamiento: cumple porque la unica clase de negocio que conoce es
 *   TorneoFacade; no tiene referencias directas a GestorPartido, MotorSimulacion,
 *   repositorios ni clases de dominio (excepto las necesarias para mostrar datos).
 */
public class SkyFutFrame extends JFrame {
    private static final Color BLACK = Color.BLACK;
    private static final Color INK = new Color(8, 8, 10);
    private static final Color WHITE = new Color(252, 252, 250);
    private static final Color MAROON = new Color(127, 16, 16);
    private static final Color PURPLE = new Color(106, 0, 230);
    private static final Color NAVY = new Color(34, 41, 128);
    private static final Color FOREST = new Color(0, 85, 68);
    private static final Color RED = new Color(229, 0, 0);
    private static final Color BLUE = new Color(50, 79, 245);
    private static final Color SKY = new Color(46, 152, 220);
    private static final Color GREEN = new Color(0, 198, 83);
    private static final Color MINT = new Color(91, 238, 203);
    private static final Color LIME = new Color(174, 232, 0);
    private static final Color PINK = new Color(230, 27, 101);
    private static final Font TITLE_FONT = new Font("Arial Black", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font BODY_FONT = new Font("Arial", Font.BOLD, 15);

    private final TorneoFacade facade;

    private final JComboBox<Equipo> equipoCombo;
    private final JComboBox<IJugador> saleCombo;
    private final JComboBox<IJugador> entraCombo;
    private final JComboBox<TacticaOption> tacticaCombo;

    private final SkyButton seleccionarEquipoButton;
    private final SkyButton iniciarTorneoButton;
    private final SkyButton simularSiguienteButton;
    private final SkyButton confirmarCambioButton;
    private final SkyButton aplicarTacticaButton;
    private final SkyButton simularSegundoTiempoButton;
    private final SkyButton verFixtureButton;
    private final SkyButton consultarEstadisticasButton;
    private final SkyButton volverJugarButton;
    private final SkyButton refrescarResultadosButton;
    private final TabButton eventosTab;
    private final TabButton plantelTab;

    private final JLabel equipoDtLabel;
    private final JLabel estadoLabel;
    private final JLabel partidoLabel;

    private final JTextArea eventosArea;
    private final JTextArea resultadosArea;
    private final JTextArea plantelArea;
    private final CardLayout contenidoTabsLayout;
    private final JPanel contenidoTabs;
    private boolean torneoCompleto;
    private boolean campeonAvisado;
    private List<EstadisticaJugadorTorneo> estadisticasFinales = List.of();

    public SkyFutFrame() {
        this.facade = new TorneoFacade();
        this.equipoCombo = new JComboBox<>();
        this.saleCombo = new JComboBox<>();
        this.entraCombo = new JComboBox<>();
        this.tacticaCombo = new JComboBox<>();
        this.seleccionarEquipoButton = SkyButton.solid("Elegir DT", PURPLE, WHITE);
        this.iniciarTorneoButton = SkyButton.solid("Iniciar torneo", GREEN, BLACK);
        this.simularSiguienteButton = SkyButton.solid("Simular siguiente partido", BLUE, WHITE);
        this.confirmarCambioButton = SkyButton.solid("Confirmar cambio", RED, WHITE);
        this.aplicarTacticaButton = SkyButton.solid("Aplicar tactica", FOREST, WHITE);
        this.simularSegundoTiempoButton = SkyButton.solid("Simular segundo tiempo", LIME, BLACK);
        this.verFixtureButton = SkyButton.solid("Ver fixture", NAVY, WHITE);
        this.consultarEstadisticasButton = SkyButton.solid("Consultar estadisticas", SKY, BLACK);
        this.volverJugarButton = SkyButton.solid("Volver a jugar", MAROON, WHITE);
        this.refrescarResultadosButton = SkyButton.solid("Refrescar resultados", BLACK, WHITE);
        this.eventosTab = new TabButton("Eventos", true);
        this.plantelTab = new TabButton("Plantel DT", false);
        this.equipoDtLabel = new JLabel("DT sin equipo");
        this.estadoLabel = new JLabel("Elegir un equipo para comenzar");
        this.partidoLabel = new JLabel("Sin partido actual");
        this.eventosArea = crearAreaTexto();
        this.resultadosArea = crearAreaTexto();
        this.plantelArea = crearAreaTexto();
        this.contenidoTabsLayout = new CardLayout();
        this.contenidoTabs = new JPanel(contenidoTabsLayout);

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
        setMinimumSize(new Dimension(1300, 720));
        setSize(1680, 860);
        setLocationRelativeTo(null);

        BackgroundPanel root = new BackgroundPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.setBorder(BorderFactory.createEmptyBorder(44, 10, 12, 10));
        setContentPane(root);

        root.add(crearPanelSuperior(), BorderLayout.NORTH);
        root.add(crearPanelCentral(), BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        RoundedPanel panel = new RoundedPanel(36, true, false);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = titulo("TORNEO");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(titulo, gbc);

        gbc.gridx = 5;
        gbc.gridwidth = 2;
        consultarEstadisticasButton.setPreferredSize(new Dimension(210, 40));
        panel.add(consultarEstadisticasButton, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(label("Equipo del DT:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        equipoCombo.setPreferredSize(new Dimension(260, 40));
        panel.add(equipoCombo, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(seleccionarEquipoButton, gbc);

        gbc.gridx = 3;
        panel.add(iniciarTorneoButton, gbc);

        gbc.gridx = 4;
        simularSiguienteButton.setPreferredSize(new Dimension(220, 40));
        panel.add(simularSiguienteButton, gbc);

        gbc.gridx = 5;
        verFixtureButton.setPreferredSize(new Dimension(130, 40));
        panel.add(verFixtureButton, gbc);

        gbc.gridx = 6;
        volverJugarButton.setPreferredSize(new Dimension(130, 40));
        panel.add(volverJugarButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(label("Seleccionado:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        equipoDtLabel.setFont(BODY_FONT);
        panel.add(equipoDtLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 4;
        estadoLabel.setFont(BODY_FONT);
        estadoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(estadoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 7;
        partidoLabel.setFont(BODY_FONT);
        partidoLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        panel.add(partidoLabel, gbc);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        JPanel izquierda = new JPanel(new BorderLayout(0, 12));
        izquierda.setOpaque(false);
        izquierda.add(crearPanelEntretiempo(), BorderLayout.NORTH);
        izquierda.add(crearPanelTabs(), BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.55;
        panel.add(izquierda, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 14, 0, 0);
        panel.add(crearPanelResumen(), gbc);

        return panel;
    }

    private JPanel crearPanelEntretiempo() {
        RoundedPanel panel = new RoundedPanel(18, true, false);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 16, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 4;
        panel.add(titulo("ENTRETIEMPO"), gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new SpeedMarks(PURPLE), gbc);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(label("Sale:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        saleCombo.setPreferredSize(new Dimension(230, 40));
        panel.add(saleCombo, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(label("Entra:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        entraCombo.setPreferredSize(new Dimension(250, 40));
        panel.add(entraCombo, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        confirmarCambioButton.setPreferredSize(new Dimension(205, 40));
        panel.add(confirmarCambioButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(label("Tactica:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        tacticaCombo.setPreferredSize(new Dimension(280, 40));
        panel.add(tacticaCombo, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        aplicarTacticaButton.setPreferredSize(new Dimension(220, 40));
        panel.add(aplicarTacticaButton, gbc);

        gbc.gridx = 4;
        simularSegundoTiempoButton.setPreferredSize(new Dimension(205, 40));
        panel.add(simularSegundoTiempoButton, gbc);

        return panel;
    }

    private JPanel crearPanelTabs() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setOpaque(false);

        JPanel tabs = new JPanel(new BorderLayout(0, 0));
        tabs.setOpaque(false);
        JPanel tabsRow = new JPanel();
        tabsRow.setOpaque(false);
        tabsRow.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        tabsRow.add(eventosTab);
        tabsRow.add(plantelTab);
        tabs.add(tabsRow, BorderLayout.WEST);

        contenidoTabs.setOpaque(false);
        contenidoTabs.add(crearContenidoDecorado(eventosArea, true), "EVENTOS");
        contenidoTabs.add(crearContenidoDecorado(plantelArea, true), "PLANTEL");

        panel.add(tabs, BorderLayout.NORTH);
        panel.add(contenidoTabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelResumen() {
        RoundedPanel panel = new RoundedPanel(18, true, false);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 14, 8, 14));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titulo("RESUMEN"), BorderLayout.WEST);
        header.add(new SpeedMarks(LIME), BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        DecoratedContent content = new DecoratedContent(false);
        content.setLayout(new BorderLayout());
        content.add(crearScrollTransparente(resultadosArea), BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);

        refrescarResultadosButton.setPreferredSize(new Dimension(220, 50));
        refrescarResultadosButton.setText("Refrescar resultados");
        panel.add(refrescarResultadosButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearContenidoDecorado(JTextArea area, boolean leftDecoration) {
        DecoratedContent panel = new DecoratedContent(leftDecoration);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(crearScrollTransparente(area), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane crearScrollTransparente(JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    private JTextArea crearAreaTexto() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setForeground(INK);
        area.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return area;
    }

    private JLabel titulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(TITLE_FONT);
        label.setForeground(BLACK);
        return label;
    }

    private JLabel label(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(LABEL_FONT);
        label.setForeground(INK);
        return label;
    }

    private void configurarRenderers() {
        estilizarCombo(equipoCombo);
        estilizarCombo(saleCombo);
        estilizarCombo(entraCombo);
        estilizarCombo(tacticaCombo);

        equipoCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(BODY_FONT);
                if (value instanceof Equipo equipo) {
                    setText(equipo.getNombre());
                }
                return this;
            }
        });

        DefaultListCellRenderer jugadorRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(BODY_FONT);
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

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(BODY_FONT);
        combo.setBackground(WHITE);
        combo.setForeground(INK);
        combo.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 1, true));
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("v");
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setBackground(WHITE);
                button.setForeground(BLACK);
                button.setFont(new Font("Arial", Font.BOLD, 18));
                return button;
            }
        });
    }

    private void configurarAcciones() {
        seleccionarEquipoButton.addActionListener(e -> ejecutarSeguro(this::seleccionarEquipo));
        iniciarTorneoButton.addActionListener(e -> ejecutarSeguro(this::iniciarTorneo));
        simularSiguienteButton.addActionListener(e -> ejecutarSeguro(this::simularSiguientePartido));
        confirmarCambioButton.addActionListener(e -> ejecutarSeguro(this::confirmarCambio));
        aplicarTacticaButton.addActionListener(e -> ejecutarSeguro(this::aplicarTactica));
        simularSegundoTiempoButton.addActionListener(e -> ejecutarSeguro(this::simularSegundoTiempo));
        verFixtureButton.addActionListener(e -> ejecutarSeguro(this::mostrarFixture));
        consultarEstadisticasButton.addActionListener(e -> ejecutarSeguro(this::mostrarEstadisticas));
        volverJugarButton.addActionListener(e -> ejecutarSeguro(this::volverAJugar));
        refrescarResultadosButton.addActionListener(e -> ejecutarSeguro(this::actualizarResultados));
        eventosTab.addActionListener(e -> seleccionarTab("EVENTOS"));
        plantelTab.addActionListener(e -> seleccionarTab("PLANTEL"));
    }

    private void seleccionarTab(String tab) {
        boolean eventos = "EVENTOS".equals(tab);
        eventosTab.setSelectedTab(eventos);
        plantelTab.setSelectedTab(!eventos);
        contenidoTabsLayout.show(contenidoTabs, tab);
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
        estadisticasFinales = List.of();
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
        if (dtQuedoEliminado(partido)) {
            mostrarInfo(mensajeEliminacion(partido));
            facade.simularRestoTorneoAutomatico();
        }
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
        consultarEstadisticasButton.setEnabled(torneoIniciado);
        volverJugarButton.setEnabled(torneoCompleto);

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

    private void mostrarEstadisticas() {
        List<EstadisticaJugadorTorneo> estadisticas = torneoCompleto && !estadisticasFinales.isEmpty()
                ? estadisticasFinales
                : facade.consultarEstadisticasEquipoDt();
        mostrarEstadisticas(estadisticas, false);
    }

    private void mostrarEstadisticas(List<EstadisticaJugadorTorneo> estadisticas, boolean modal) {
        String[] columnas = {
                "Jugador", "Posicion", "Partidos", "Goles", "Amarillas",
                "Lesiones", "Minutos", "Rendimiento promedio"
        };
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (EstadisticaJugadorTorneo estadistica : estadisticas) {
            model.addRow(new Object[] {
                    estadistica.jugador(),
                    estadistica.posicion(),
                    estadistica.partidos(),
                    estadistica.goles(),
                    estadistica.tarjetasAmarillas(),
                    estadistica.lesiones(),
                    estadistica.minutosJugados(),
                    String.format("%.2f", estadistica.rendimientoPromedio())
            });
        }

        JTable tabla = new JTable(model);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);
        tabla.setFont(BODY_FONT);
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(LABEL_FONT);

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        if (estadisticas.isEmpty()) {
            contenido.add(
                    new JLabel("No hay estadisticas registradas para este torneo.", SwingConstants.CENTER),
                    BorderLayout.NORTH);
        }
        contenido.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Equipo DT", contenido);

        JDialog dialog = new JDialog(this, "Estadisticas del torneo", modal);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(tabs, BorderLayout.CENTER);
        dialog.setSize(1080, 540);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private boolean dtQuedoEliminado(Partido partido) {
        Equipo equipoDt = facade.getEquipoDt();
        return partido != null
                && equipoDt != null
                && (partido.getLocal().getId() == equipoDt.getId()
                    || partido.getVisitante().getId() == equipoDt.getId())
                && partido.getGanador() != null
                && partido.getGanador().getId() != equipoDt.getId();
    }

    private String mensajeEliminacion(Partido partido) {
        Equipo equipoDt = facade.getEquipoDt();
        String rival = partido.getLocal().getId() == equipoDt.getId()
                ? partido.getVisitante().getNombre()
                : partido.getLocal().getNombre();
        String ronda = facade.consultarFixture().stream()
                .filter(p -> p.partidoId() == partido.getId())
                .map(FixturePartido::faseNombre)
                .findFirst()
                .orElse("esta ronda");

        return "Quedaste eliminado en " + ronda + " contra " + rival
                + ". Ahora se simulara automaticamente el resto del torneo!";
    }

    private void volverAJugar() {
        facade.reiniciarParaNuevoTorneo();
        torneoCompleto = false;
        campeonAvisado = false;
        estadisticasFinales = List.of();
        equipoDtLabel.setText("DT sin equipo");
        estadoLabel.setText("Elegir un equipo para comenzar");
        partidoLabel.setText("Sin partido actual");
        eventosArea.setText("");
        resultadosArea.setText("Inicia el torneo para ver resultados.\n");
        plantelArea.setText("Todavia no hay equipo del DT.\n");
        cargarEquipos();
        actualizarEstadoControles();
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
        estadisticasFinales = List.copyOf(facade.consultarEstadisticasEquipoDt());
        mostrarEstadisticas(estadisticasFinales, true);
        facade.limpiarEstadisticasTorneoActual();
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
                + " | rendimiento " + String.format("%.2f", jugador.getRendimiento())
                + " | " + describirDecoradores(jugador);
    }

    private String describirDecoradores(IJugador jugador) {
        List<String> decoradores = new ArrayList<>();
        IJugador actual = jugador;
        while (actual instanceof JugadorDecorator decorator) {
            decoradores.add(decorator.getNombreDecorador() + " (" + decorator.getImpactoDecorador() + ")");
            actual = decorator.getJugadorDecorado();
        }
        return decoradores.isEmpty() ? "Normal" : String.join(", ", decoradores);
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

    private static class BackgroundPanel extends JPanel {
        BackgroundPanel() {
            setBackground(BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(BLACK);
            g2.fillRect(0, 0, w, h);
            g2.setPaint(new GradientPaint(w - 500, 0, new Color(105, 0, 255), w - 120, 0, new Color(117, 0, 220)));
            g2.fillOval(w - 520, -210, 520, 500);
            g2.fillRect(w - 310, 0, 260, 48);

            int[] colors = {0x6A00E6, 0x324FF5, 0x5BEECC, 0xAEE800, 0xE50000};
            int y = 44;
            for (int color : colors) {
                g2.setColor(new Color(color));
                g2.fillRoundRect(-34, y, 86, 84, 26, 26);
                y += 31;
            }
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final boolean shadow;
        private final boolean topStripes;

        RoundedPanel(int radius, boolean shadow, boolean topStripes) {
            this.radius = radius;
            this.shadow = shadow;
            this.topStripes = topStripes;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
            if (shadow) {
                g2.setColor(new Color(0, 0, 0, 36));
                g2.fillRoundRect(2, 3, w - 4, h - 4, radius, radius);
            }
            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, w - 4, h - 5, radius, radius);
            g2.setColor(new Color(230, 230, 230));
            g2.drawRoundRect(0, 0, w - 4, h - 5, radius, radius);
            if (topStripes) {
                g2.setColor(PURPLE);
                for (int i = 0; i < 5; i++) {
                    g2.fillPolygon(
                            new int[] {w - 126 + i * 20, w - 110 + i * 20, w - 124 + i * 20, w - 140 + i * 20},
                            new int[] {22, 22, 44, 44},
                            4);
                }
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class DecoratedContent extends JPanel {
        private final boolean leftDecoration;

        DecoratedContent(boolean leftDecoration) {
            this.leftDecoration = leftDecoration;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, w - 2, h - 2, 16, 16);
            g2.setColor(new Color(224, 224, 224));
            g2.drawRoundRect(0, 0, w - 2, h - 2, 16, 16);
            if (leftDecoration) {
                drawRainbowCorner(g2, -170, h - 128, false);
            }
            g2.dispose();
            super.paintComponent(g);
        }

        private void drawRainbowCorner(Graphics2D g2, int x, int y, boolean soft) {
            Color[] colors = soft
                    ? new Color[] {new Color(169, 226, 240, 210), new Color(198, 175, 239, 180),
                            new Color(255, 169, 178, 165), new Color(210, 246, 87, 150)}
                    : new Color[] {LIME, RED, PURPLE, new Color(0, 207, 196)};
            int[] sizes = soft ? new int[] {260, 205, 150, 92} : new int[] {330, 275, 220, 165};
            for (int i = 0; i < colors.length; i++) {
                g2.setColor(colors[i]);
                g2.fillRoundRect(x + i * 35, y + i * 52, sizes[i], sizes[i], 78, 78);
            }
        }
    }

    private static class SkyButton extends JButton {
        private final Color accent;
        private final Color text;
        private final boolean filled;
        private final boolean neutral;
        private boolean hover;

        private SkyButton(String text, Color accent, Color textColor, boolean filled, boolean neutral) {
            super(text);
            this.accent = accent;
            this.text = textColor;
            this.filled = filled;
            this.neutral = neutral;
            setFont(BODY_FONT);
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        static SkyButton solid(String text, Color accent, Color textColor) {
            return new SkyButton(text, accent, textColor, true, false);
        }

        static SkyButton outline(String text, Color accent, Color fill) {
            return new SkyButton(text, accent, accent, false, false);
        }

        static SkyButton neutral(String text) {
            return new SkyButton(text, new Color(215, 215, 215), new Color(120, 120, 120), true, true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
            boolean enabled = isEnabled();
            Color bg = filled ? accent : WHITE;
            Color fg = filled ? text : accent;
            if (neutral || !enabled) {
                bg = enabled ? new Color(232, 232, 232) : new Color(238, 238, 238);
                fg = enabled ? new Color(118, 118, 118) : new Color(156, 156, 156);
            } else if (hover) {
                bg = filled ? mix(accent, WHITE, 0.12) : mix(accent, WHITE, 0.92);
            }
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
            g2.setColor(enabled && !neutral ? mix(accent, BLACK, 0.18) : new Color(205, 205, 205));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(fg);
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }

        private static Color mix(Color color, Color target, double amount) {
            double keep = 1.0 - amount;
            int r = (int) Math.round(color.getRed() * keep + target.getRed() * amount);
            int g = (int) Math.round(color.getGreen() * keep + target.getGreen() * amount);
            int b = (int) Math.round(color.getBlue() * keep + target.getBlue() * amount);
            return new Color(r, g, b);
        }
    }

    private static class TabButton extends JButton {
        private boolean selected;

        TabButton(String text, boolean selected) {
            super(text);
            this.selected = selected;
            setPreferredSize(new Dimension(150, 46));
            setFont(BODY_FONT);
            setBorder(BorderFactory.createEmptyBorder());
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setSelectedTab(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(selected ? PURPLE : WHITE);
            g2.fillRoundRect(0, 0, w, h + 18, 18, 18);
            g2.setColor(selected ? PURPLE : new Color(224, 224, 224));
            g2.drawRoundRect(0, 0, w - 1, h + 18, 18, 18);
            g2.setFont(getFont());
            g2.setColor(selected ? WHITE : BLACK);
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    private static class SpeedMarks extends JComponent {
        private final Color color;

        SpeedMarks(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(96, 28));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            g2.setColor(color);
            for (int i = 0; i < 5; i++) {
                int x = i * 18;
                g2.fillPolygon(new int[] {x + 18, x + 30, x + 18, x + 6}, new int[] {2, 2, 24, 24}, 4);
            }
            g2.dispose();
        }
    }

    private static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    private static void drawCentered(Graphics2D g2, String text, int x, int y, int width) {
        FontMetrics metrics = g2.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g2.drawString(text, textX, y);
    }
}
