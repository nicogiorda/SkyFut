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
    private static final int CARD_W = 185;
    private static final int CARD_H = 84;
    private static final int SMALL_W = 150;
    private static final int SMALL_H = 66;
    private static final Color BLACK = Color.BLACK;
    private static final Color WHITE = new Color(252, 252, 250);
    private static final Color BLUE = new Color(50, 79, 245);
    private static final Color SKY = new Color(46, 152, 220);
    private static final Color PURPLE = new Color(106, 0, 230);
    private static final Color RED = new Color(229, 0, 0);
    private static final Color LIME = new Color(174, 232, 0);
    private static final Font TITLE = new Font("Arial Black", Font.BOLD, 18);
    private static final Font TEAM = new Font("Arial", Font.BOLD, 13);
    private static final Font MUTED = new Font("Arial", Font.BOLD, 13);

    private final List<FixturePartido> fixture;

    public FixturePanel(List<FixturePartido> fixture) {
        this.fixture = List.copyOf(fixture);
        setBackground(BLACK);
        setPreferredSize(new Dimension(1380, 720));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = smooth(g);
        int w = getWidth();
        int h = getHeight();

        drawBackground(g2, w, h);
        Map<Integer, List<FixturePartido>> porFase = agruparPorFase();

        Layout layout = new Layout(w, h);
        drawTitles(g2, layout);
        drawConnectors(g2, layout);
        drawMatches(g2, porFase, layout);

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(13, 15, 27));
        g2.fillRect(0, 0, w, h);

        drawRainbowArc(g2, w - 610, -230, 760, 430, false);
        drawRainbowArc(g2, -280, h - 310, 760, 430, true);

        g2.setColor(WHITE);
        g2.fillRoundRect(0, 52, w - 16, h - 54, 72, 72);
        g2.setColor(new Color(236, 236, 236));
        g2.drawRoundRect(0, 52, w - 16, h - 54, 72, 72);
    }

    private void drawRainbowArc(Graphics2D g2, int x, int y, int w, int h, boolean lower) {
        Color[] colors = {PURPLE, BLUE, new Color(0, 185, 225), new Color(0, 90, 70), new Color(0, 200, 0), LIME, RED};
        int inset = 0;
        for (Color color : colors) {
            g2.setColor(color);
            g2.fillOval(x + inset, y + inset, w - inset * 2, h - inset * 2);
            inset += lower ? 24 : 42;
        }
        g2.setColor(new Color(13, 15, 27));
        g2.fillOval(x + inset, y + inset, w - inset * 2, h - inset * 2);
    }

    private void drawTitles(Graphics2D g2, Layout l) {
        drawTitle(g2, "Octavos", l.leftOctavosX + 48, l.titleY, BLUE);
        drawTitle(g2, "Cuartos", l.leftCuartosX + 40, l.titleY + 8, SKY);
        drawTitle(g2, "Semis", l.leftSemiX + 38, l.titleY + 88, PURPLE);
        drawTitle(g2, "Final", l.finalX + 58, l.titleY + 184, RED);
        drawTitle(g2, "Semis", l.rightSemiX + 38, l.titleY + 88, PURPLE);
        drawTitle(g2, "Cuartos", l.rightCuartosX + 40, l.titleY + 8, SKY);
        drawTitle(g2, "Octavos", l.rightOctavosX + 48, l.titleY, BLUE);
    }

    private void drawTitle(Graphics2D g2, String text, int x, int y, Color color) {
        g2.setFont(TITLE);
        g2.setColor(color);
        g2.drawString(text, x, y);
        FontMetrics metrics = g2.getFontMetrics();
        int width = Math.min(34, metrics.stringWidth(text));
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + (metrics.stringWidth(text) - width) / 2, y + 12,
                x + (metrics.stringWidth(text) + width) / 2, y + 12);
    }

    private void drawConnectors(Graphics2D g2, Layout l) {
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(BLACK);
        connectPair(g2, l.leftOctavosX, l.leftOctavosY[0], CARD_W, CARD_H, l.leftCuartosX, l.leftCuartosY[0], SMALL_W, SMALL_H, false);
        connectPair(g2, l.leftOctavosX, l.leftOctavosY[2], CARD_W, CARD_H, l.leftCuartosX, l.leftCuartosY[1], SMALL_W, SMALL_H, false);
        connectPair(g2, l.rightOctavosX, l.rightOctavosY[0], CARD_W, CARD_H, l.rightCuartosX, l.rightCuartosY[0], SMALL_W, SMALL_H, true);
        connectPair(g2, l.rightOctavosX, l.rightOctavosY[2], CARD_W, CARD_H, l.rightCuartosX, l.rightCuartosY[1], SMALL_W, SMALL_H, true);

        g2.setColor(PURPLE);
        connectPair(g2, l.leftCuartosX, l.leftCuartosY[0], SMALL_W, SMALL_H, l.leftSemiX, l.semiY, SMALL_W, SMALL_H, false);
        connectPair(g2, l.rightCuartosX, l.rightCuartosY[0], SMALL_W, SMALL_H, l.rightSemiX, l.semiY, SMALL_W, SMALL_H, true);
        connectSingle(g2, l.leftSemiX, l.semiY, SMALL_W, SMALL_H, l.finalX, l.finalY, CARD_W, SMALL_H, false);
        connectSingle(g2, l.rightSemiX, l.semiY, SMALL_W, SMALL_H, l.finalX, l.finalY, CARD_W, SMALL_H, true);
    }

    private void connectPair(Graphics2D g2, int fromX, int fromY, int fromW, int fromH,
            int toX, int toY, int toW, int toH, boolean rightSide) {
        connectSingle(g2, fromX, fromY, fromW, fromH, toX, toY, toW, toH, rightSide);
        connectSingle(g2, fromX, fromY + 112, fromW, fromH, toX, toY, toW, toH, rightSide);
    }

    private void connectSingle(Graphics2D g2, int fromX, int fromY, int fromW, int fromH,
            int toX, int toY, int toW, int toH, boolean rightSide) {
        int startX = rightSide ? fromX : fromX + fromW;
        int endX = rightSide ? toX + toW : toX;
        int startY = fromY + fromH / 2;
        int endY = toY + toH / 2;
        int midX = (startX + endX) / 2;
        g2.drawLine(startX, startY, midX, startY);
        g2.drawLine(midX, startY, midX, endY);
        g2.drawLine(midX, endY, endX, endY);
    }

    private void drawMatches(Graphics2D g2, Map<Integer, List<FixturePartido>> porFase, Layout l) {
        for (int i = 0; i < 4; i++) {
            drawMatch(g2, obtenerPartido(porFase, 1, i + 1), l.leftOctavosX, l.leftOctavosY[i], CARD_W, CARD_H,
                    colorForIndex(i), false);
            drawMatch(g2, obtenerPartido(porFase, 1, i + 5), l.rightOctavosX, l.rightOctavosY[i], CARD_W, CARD_H,
                    colorForIndex(i), true);
        }

        drawMatch(g2, obtenerPartido(porFase, 2, 1), l.leftCuartosX, l.leftCuartosY[0], SMALL_W, SMALL_H, SKY, false);
        drawMatch(g2, obtenerPartido(porFase, 2, 2), l.leftCuartosX, l.leftCuartosY[1], SMALL_W, SMALL_H, SKY, false);
        drawMatch(g2, obtenerPartido(porFase, 2, 3), l.rightCuartosX, l.rightCuartosY[0], SMALL_W, SMALL_H, SKY, true);
        drawMatch(g2, obtenerPartido(porFase, 2, 4), l.rightCuartosX, l.rightCuartosY[1], SMALL_W, SMALL_H, SKY, true);

        drawMatch(g2, obtenerPartido(porFase, 3, 1), l.leftSemiX, l.semiY, SMALL_W, SMALL_H, PURPLE, false);
        drawMatch(g2, obtenerPartido(porFase, 3, 2), l.rightSemiX, l.semiY, SMALL_W, SMALL_H, PURPLE, true);
        drawMatch(g2, obtenerPartido(porFase, 4, 1), l.finalX, l.finalY, CARD_W, SMALL_H, RED, false);
    }

    private Color colorForIndex(int index) {
        return switch (index) {
            case 0 -> BLUE;
            case 1 -> SKY;
            case 2 -> PURPLE;
            default -> new Color(92, 184, 24);
        };
    }

    private void drawMatch(Graphics2D g2, FixturePartido partido, int x, int y, int w, int h, Color accent, boolean accentRight) {
        drawShadow(g2, x, y, w, h);
        g2.setColor(WHITE);
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setColor(new Color(230, 230, 230));
        g2.drawRoundRect(x, y, w, h, 12, 12);

        int accentX = accentRight ? x + w - 6 : x;
        g2.setColor(accent);
        g2.fillRoundRect(accentX, y + 10, 6, h - 20, 6, 6);

        if (partido == null) {
            g2.setFont(MUTED);
            g2.setColor(new Color(116, 116, 122));
            drawCentered(g2, "Pendiente", x, y + h / 2 + 5, w);
            return;
        }

        g2.setFont(TEAM);
        g2.setColor(BLACK);
        int textX = accentRight ? x + 18 : x + 24;
        int y1 = y + (h >= CARD_H ? 32 : 40);
        String local = textoEquipo(partido.equipoLocal(), partido.finalizado() ? partido.golesLocal() : null);
        String visitante = textoEquipo(partido.equipoVisitante(), partido.finalizado() ? partido.golesVisitante() : null);
        if (partido.finalizado() && partido.ganador() != null) {
            if (partido.ganador().equals(partido.equipoLocal())) {
                local = "> " + local;
            } else if (partido.ganador().equals(partido.equipoVisitante())) {
                visitante = "> " + visitante;
            }
        }

        g2.drawString(recortar(g2, local, w - 34), textX, y1);
        if (h >= CARD_H) {
            g2.drawString(recortar(g2, visitante, w - 34), textX, y + 63);
        }
    }

    private void drawShadow(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(0, 0, 0, 25));
        g2.fillRoundRect(x + 3, y + 5, w, h, 12, 12);
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

    private void drawCentered(Graphics2D g2, String text, int x, int y, int width) {
        FontMetrics metrics = g2.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g2.drawString(text, textX, y);
    }

    private Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    private static class Layout {
        final int titleY = 140;
        final int leftOctavosX = 42;
        final int leftCuartosX = 295;
        final int leftSemiX;
        final int finalX;
        final int rightSemiX;
        final int rightCuartosX;
        final int rightOctavosX;
        final int[] leftOctavosY = {190, 302, 414, 526};
        final int[] rightOctavosY = {190, 302, 414, 526};
        final int[] leftCuartosY = {262, 486};
        final int[] rightCuartosY = {262, 486};
        final int semiY = 386;
        final int finalY = 394;

        Layout(int width, int height) {
            finalX = width / 2 - CARD_W / 2;
            leftSemiX = finalX - 245;
            rightSemiX = finalX + CARD_W + 95;
            rightCuartosX = width - 295 - SMALL_W;
            rightOctavosX = width - 42 - CARD_W;
        }
    }
}
