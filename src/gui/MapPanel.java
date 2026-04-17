package gui;

import evolution.entorno.Agente;
import evolution.entorno.Entorno;
import java.awt.*;
import javax.swing.*;

public class MapPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Entorno entorno;
    private Agente agente;

    public MapPanel() {
        setBackground(new Color(30, 30, 30));
        setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Arena del Gusano (" + Entorno.ANCHO + "x" + Entorno.ALTO + ")",
                0,
                0,
                null,
                Color.WHITE
            )
        );
    }

    public void updateView(Entorno e, Agente a) {
        this.entorno = e;
        this.agente = a;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (entorno == null) return;

        Graphics2D g2 = (Graphics2D) g;
        // Anti-aliasing para que el gusano se vea suave
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Calculamos el tamaño de celda para que encaje perfecto en el panel
        int cellW = (getWidth() - 20) / Entorno.ANCHO;
        int cellH = (getHeight() - 30) / Entorno.ALTO;
        int cellSize = Math.min(cellW, cellH);

        int offsetX = (getWidth() - (Entorno.ANCHO * cellSize)) / 2;
        int offsetY = (getHeight() - (Entorno.ALTO * cellSize)) / 2 + 10;

        for (int y = 0; y < Entorno.ALTO; y++) {
            for (int x = 0; x < Entorno.ANCHO; x++) {
                int px = offsetX + (x * cellSize);
                int py = offsetY + (y * cellSize);

                // 1. Fondo de la celda
                g2.setColor(new Color(40, 40, 40));
                g2.fillRect(px, py, cellSize, cellSize);

                // 2. Dibujar Entidades del Mapa
                int tipo = entorno.getTipoCasilla(x, y);
                switch (tipo) {
                    case Entorno.MURO:
                        g2.setColor(new Color(100, 100, 100)); // Gris bloque
                        g2.fillRect(px, py, cellSize, cellSize);
                        break;
                    case Entorno.COMIDA:
                        g2.setColor(new Color(46, 204, 113)); // Verde nutritivo
                        g2.fillOval(px + 2, py + 2, cellSize - 4, cellSize - 4);
                        break;
                    case Entorno.DESCANSO:
                        g2.setColor(new Color(20, 20, 60)); // Zona oscura/azulada
                        g2.fillRect(px, py, cellSize, cellSize);
                        break;
                }

                // Malla sutil
                g2.setColor(new Color(50, 50, 50));
                g2.drawRect(px, py, cellSize, cellSize);
            }
        }

        // 3. Dibujar Gusano
        if (agente != null && agente.vivo) {
            int px = offsetX + (agente.x * cellSize);
            int py = offsetY + (agente.y * cellSize);

            // Si duerme es azul, si está despierto es amarillo/naranja
            g2.setColor(
                agente.dormido
                    ? new Color(52, 152, 219)
                    : new Color(241, 196, 15)
            );
            g2.fillOval(px + 1, py + 1, cellSize - 2, cellSize - 2);
        }

        // 4. Dibujar HUD (Energía y Sueño)
        if (agente != null) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            String estado = agente.vivo
                ? (agente.dormido ? "DURMIENDO" : "VIVO")
                : "MUERTO";
            g2.drawString(
                String.format(
                    "Energía: %.1f | Sueño: %.1f | Estado: %s",
                    agente.energia,
                    agente.sueno,
                    estado
                ),
                20,
                20
            );
        }
    }
}
