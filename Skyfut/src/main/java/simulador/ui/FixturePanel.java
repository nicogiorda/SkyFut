package simulador.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import simulador.dto.FixturePartido;

public class FixturePanel extends JPanel {
    private static final int BOX_W = 190;
    private static final int BOX_H = 48;

    private final List<FixturePartido> fixture;

    public FixturePanel(List<FixturePartido> fixture) {
        this.fixture = List.copyOf(fixture);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1220, 560));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(35, 35, 35));

        Map<Integer, List<FixturePartido>> porFase = agruparPorFase();

        int centerX = getWidth() / 2;
        int finalY = 238;

        drawTitle(g2, "Octavos", 35, 24);
        drawTitle(g2, "Cuartos", 255, 72);
        drawTitle(g2, "Semis", 475, 170);
        drawTitle(g2, "Final", centerX - BOX_W / 2, 218);
        drawTitle(g2, "Semis", getWidth() - 475 - BOX_W, 170);
        drawTitle(g2, "Cuartos", getWidth() - 255 - BOX_W, 72);
        drawTitle(g2, "Octavos", getWidth() - 35 - BOX_W, 24);

        int[] leftOctavosY = {42, 142, 242, 342};
        int[] leftCuartosY = {92, 292};
        int leftSemiY = 192;

        int leftOctavosX = 35;
        int leftCuartosX = 255;
        int leftSemiX = 475;
        int finalX = centerX - BOX_W / 2;

        drawSide(g2, porFase, leftOctavosX, leftCuartosX, leftSemiX, finalX,
                leftOctavosY, leftCuartosY, leftSemiY, finalY, false);

        int rightOctavosX = getWidth() - 35 - BOX_W;
        int rightCuartosX = getWidth() - 255 - BOX_W;
        int rightSemiX = getWidth() - 475 - BOX_W;

        drawSide(g2, porFase, rightOctavosX, rightCuartosX, rightSemiX, finalX,
                leftOctavosY, leftCuartosY, leftSemiY, finalY, true);

        drawMatch(g2, obtenerPartido(porFase, 4, 1), finalX, finalY, true);
        g2.dispose();
    }

    private void drawSide(
            Graphics2D g2,
            Map<Integer, List<FixturePartido>> porFase,
            int octavosX,
            int cuartosX,
            int semiX,
            int finalX,
            int[] octavosY,
            int[] cuartosY,
            int semiY,
            int finalY,
            boolean derecha) {
        int octavosOffset = derecha ? 4 : 0;
        int cuartosOffset = derecha ? 2 : 0;
        int semiOrden = derecha ? 2 : 1;

        for (int i = 0; i < 4; i++) {
            drawMatch(g2, obtenerPartido(porFase, 1, octavosOffset + i + 1), octavosX, octavosY[i], false);
        }
        for (int i = 0; i < 2; i++) {
            drawMatch(g2, obtenerPartido(porFase, 2, cuartosOffset + i + 1), cuartosX, cuartosY[i], false);
        }
        drawMatch(g2, obtenerPartido(porFase, 3, semiOrden), semiX, semiY, false);

        conectar(g2, octavosX, octavosY[0], cuartosX, cuartosY[0], derecha);
        conectar(g2, octavosX, octavosY[1], cuartosX, cuartosY[0], derecha);
        conectar(g2, octavosX, octavosY[2], cuartosX, cuartosY[1], derecha);
        conectar(g2, octavosX, octavosY[3], cuartosX, cuartosY[1], derecha);
        conectar(g2, cuartosX, cuartosY[0], semiX, semiY, derecha);
        conectar(g2, cuartosX, cuartosY[1], semiX, semiY, derecha);
        conectar(g2, semiX, semiY, finalX, finalY, derecha);
    }

    private void drawMatch(Graphics2D g2, FixturePartido partido, int x, int y, boolean finalBox) {
        Color border = finalBox ? new Color(22, 91, 170) : new Color(25, 25, 25);
        Color fill = finalBox ? new Color(237, 246, 255) : Color.WHITE;
        g2.setColor(fill);
        g2.fillRoundRect(x, y, BOX_W, BOX_H, 8, 8);
        g2.setColor(border);
        g2.drawRoundRect(x, y, BOX_W, BOX_H, 8, 8);

        if (partido == null) {
            g2.setColor(new Color(130, 130, 130));
            drawCentered(g2, "Pendiente", x, y + 28, BOX_W);
            return;
        }

        g2.setColor(new Color(20, 20, 20));
        String local = textoEquipo(partido.equipoLocal(), partido.finalizado() ? partido.golesLocal() : null);
        String visitante = textoEquipo(partido.equipoVisitante(), partido.finalizado() ? partido.golesVisitante() : null);
        if (partido.finalizado() && partido.ganador() != null) {
            if (partido.ganador().equals(partido.equipoLocal())) {
                local = "* " + local;
            } else if (partido.ganador().equals(partido.equipoVisitante())) {
                visitante = "* " + visitante;
            }
        }
        g2.drawString(recortar(g2, local, BOX_W - 16), x + 8, y + 19);
        g2.drawString(recortar(g2, visitante, BOX_W - 16), x + 8, y + 39);
    }

    private void conectar(Graphics2D g2, int x1, int y1, int x2, int y2, boolean derecha) {
        int startX = derecha ? x1 : x1 + BOX_W;
        int endX = derecha ? x2 + BOX_W : x2;
        int startY = y1 + BOX_H / 2;
        int endY = y2 + BOX_H / 2;
        int midX = (startX + endX) / 2;

        g2.setColor(new Color(35, 35, 35));
        g2.drawLine(startX, startY, midX, startY);
        g2.drawLine(midX, startY, midX, endY);
        g2.drawLine(midX, endY, endX, endY);
    }

    private Map<Integer, List<FixturePartido>> agruparPorFase() {
        List<FixturePartido> ordenados = new ArrayList<>(fixture);
        ordenados.sort(Comparator
                .comparingInt(FixturePartido::faseOrden)
                .thenComparingInt(FixturePartido::ordenPartido));
        Map<Integer, List<FixturePartido>> porFase = new LinkedHashMap<>();
        for (FixturePartido partido : ordenados) {
            porFase.computeIfAbsent(partido.faseOrden(), key -> new ArrayList<>()).add(partido);
        }
        return porFase;
    }

    private FixturePartido obtenerPartido(Map<Integer, List<FixturePartido>> porFase, int fase, int orden) {
        List<FixturePartido> partidos = porFase.get(fase);
        if (partidos == null || orden < 1 || orden > partidos.size()) {
            return null;
        }
        return partidos.get(orden - 1);
    }

    private String textoEquipo(String equipo, Integer goles) {
        if (equipo == null) {
            return "Pendiente";
        }
        return goles == null ? equipo : equipo + " " + goles;
    }

    private String recortar(Graphics2D g2, String texto, int anchoMaximo) {
        FontMetrics metrics = g2.getFontMetrics();
        if (metrics.stringWidth(texto) <= anchoMaximo) {
            return texto;
        }
        String sufijo = "...";
        String base = texto;
        while (!base.isEmpty() && metrics.stringWidth(base + sufijo) > anchoMaximo) {
            base = base.substring(0, base.length() - 1);
        }
        return base + sufijo;
    }

    private void drawTitle(Graphics2D g2, String title, int x, int y) {
        Font original = g2.getFont();
        g2.setFont(original.deriveFont(Font.BOLD, 14f));
        g2.setColor(new Color(45, 45, 45));
        drawCentered(g2, title, x, y, BOX_W);
        g2.setFont(original);
    }

    private void drawCentered(Graphics2D g2, String text, int x, int y, int width) {
        FontMetrics metrics = g2.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g2.drawString(text, textX, y);
    }
}
