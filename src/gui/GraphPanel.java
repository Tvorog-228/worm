package gui;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class GraphPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Double> historyBestAbs = new ArrayList<>();
    private List<Double> historyAvg = new ArrayList<>();
    private List<Double> historyBestInd = new ArrayList<>();

    private final int PADDING = 60;
    private final Color COLOR_BEST_ABS = new Color(41, 128, 185); // Azul (Mejor Absoluto)
    private final Color COLOR_AVG = new Color(39, 174, 96); // Verde (Media)
    private final Color COLOR_BEST_GEN = new Color(231, 76, 60); // Rojo (Mejor Generación)
    private final Color COLOR_GRID = new Color(230, 230, 230);

    public GraphPanel() {
        setBackground(Color.WHITE);
        setBorder(
            BorderFactory.createTitledBorder("Evolución del Rendimiento")
        );
    }

    public void updateData(
        List<Double> mejores,
        List<Double> medias,
        List<Double> mejoresPorGeneracion
    ) {
        this.historyBestAbs = new ArrayList<>(mejores);
        this.historyAvg = new ArrayList<>(medias);
        this.historyBestInd = new ArrayList<>(mejoresPorGeneracion);
        repaint();
    }

    public void reset() {
        historyBestAbs.clear();
        historyAvg.clear();
        historyBestInd.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        int w = getWidth();
        int h = getHeight();

        double maxVal = getGlobalMax();
        double minVal = getGlobalMin();
        double range = maxVal - minVal;
        if (range == 0) range = 1.0;

        drawAxesAndLabels(g2, w, h, minVal, maxVal);

        if (historyBestAbs.size() < 2) return;

        int plotW = w - 2 * PADDING;
        int plotH = h - 2 * PADDING;

        for (int i = 0; i < historyBestAbs.size() - 1; i++) {
            float x1 =
                PADDING + (i * (float) plotW) / (historyBestAbs.size() - 1);
            float x2 =
                PADDING +
                ((i + 1) * (float) plotW) / (historyBestAbs.size() - 1);

            // 1. Dibujar Mejor de la Generación (ROJO)
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(COLOR_BEST_GEN);
            float yg1 = (float) (h -
                PADDING -
                ((historyBestInd.get(i) - minVal) * plotH) / range);
            float yg2 = (float) (h -
                PADDING -
                ((historyBestInd.get(i + 1) - minVal) * plotH) / range);
            g2.draw(new Line2D.Float(x1, yg1, x2, yg2));

            // 2. Dibujar Media de la Población (VERDE)
            g2.setStroke(
                new BasicStroke(
                    1.5f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND,
                    1.0f,
                    new float[] { 5.0f },
                    0.0f
                )
            );
            g2.setColor(COLOR_AVG);
            float ya1 = (float) (h -
                PADDING -
                ((historyAvg.get(i) - minVal) * plotH) / range);
            float ya2 = (float) (h -
                PADDING -
                ((historyAvg.get(i + 1) - minVal) * plotH) / range);
            g2.draw(new Line2D.Float(x1, ya1, x2, ya2));

            // 3. Dibujar Mejor Absoluto (AZUL)
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(COLOR_BEST_ABS);
            float y1 = (float) (h -
                PADDING -
                ((historyBestAbs.get(i) - minVal) * plotH) / range);
            float y2 = (float) (h -
                PADDING -
                ((historyBestAbs.get(i + 1) - minVal) * plotH) / range);
            g2.draw(new Line2D.Float(x1, y1, x2, y2));
        }

        drawLegend(g2, w);
    }

    private void drawAxesAndLabels(
        Graphics2D g2,
        int w,
        int h,
        double minVal,
        double maxVal
    ) {
        int plotH = h - 2 * PADDING;
        int plotW = w - 2 * PADDING;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));

        for (int i = 0; i <= 5; i++) {
            int y = h - PADDING - ((i * plotH) / 5);
            double val = minVal + ((i * (maxVal - minVal)) / 5);
            g2.setColor(COLOR_GRID);
            g2.drawLine(PADDING, y, w - PADDING, y);
            g2.setColor(Color.DARK_GRAY);
            String label = String.format("%.0f", val);
            g2.drawString(label, PADDING - 35, y + 5);
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.translate(PADDING - 45, h / 2);
        g2.rotate(-Math.PI / 2);
        g2.drawString("Fitness", -20, 0);
        g2.rotate(Math.PI / 2);
        g2.translate(-(PADDING - 45), -(h / 2));

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        int numGens = historyBestAbs.size();
        if (numGens > 0) {
            for (int i = 0; i <= 4; i++) {
                int x = PADDING + ((i * plotW) / 4);
                int genLabel = ((i * (numGens - 1)) / 4);
                g2.setColor(COLOR_GRID);
                g2.drawLine(x, PADDING, x, h - PADDING);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(
                    String.valueOf(genLabel),
                    x - 5,
                    h - PADDING + 20
                );
            }
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("Generación", w / 2 - 30, h - 15);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(PADDING, h - PADDING, w - PADDING, h - PADDING);
        g2.drawLine(PADDING, PADDING, PADDING, h - PADDING);
    }

    private void drawLegend(Graphics2D g2, int w) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));

        // Mejor Absoluto
        g2.setColor(COLOR_BEST_ABS);
        g2.fillRect(w - 160, 25, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawString("Mejor Absoluto", w - 140, 35);

        // Mejor Generación
        g2.setColor(COLOR_BEST_GEN);
        g2.fillRect(w - 160, 45, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawString("Mejor Generación", w - 140, 55);

        // Media
        g2.setColor(COLOR_AVG);
        g2.fillRect(w - 160, 65, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawString("Media Población", w - 140, 75);
    }

    private double getGlobalMax() {
        if (historyBestAbs.isEmpty()) return 100;
        double max = Collections.max(historyBestAbs);
        if (!historyAvg.isEmpty()) max = Math.max(
            max,
            Collections.max(historyAvg)
        );
        if (!historyBestInd.isEmpty()) max = Math.max(
            max,
            Collections.max(historyBestInd)
        );
        return max;
    }

    private double getGlobalMin() {
        if (historyAvg.isEmpty()) return 0;
        double min = Collections.min(historyAvg);
        if (!historyBestAbs.isEmpty()) min = Math.min(
            min,
            Collections.min(historyBestAbs)
        );
        if (!historyBestInd.isEmpty()) min = Math.min(
            min,
            Collections.min(historyBestInd)
        );
        return min;
    }
}
