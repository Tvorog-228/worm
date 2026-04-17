package gui;

import evolution.Individual;
import evolution.entorno.Agente;
import evolution.entorno.Contexto;
import evolution.entorno.Entorno;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import logic.Algoritmo;
import logic.AlgoritmoListener;

public class MainGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private JSpinner spinPop, spinGens, spinSeed, spinBloat, spinDepth;
    private JSpinner spinCrossover, spinMutation, spinElitism;
    private JComboBox<String> comboMutation;
    private JComboBox<String> comboSelection;

    private JButton btnSimulate;
    private Individual mejorGlobal;

    private JTextPane txtPhenotype;
    private MapPanel mapPanel;
    private GraphPanel graphPanel;

    public MainGUI() {
        setTitle("AI Lab - Evolución de C. Elegans");
        setSize(1300, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Panel de Configuración
        JPanel pnlConfig = new JPanel();
        pnlConfig.setLayout(new BoxLayout(pnlConfig, BoxLayout.Y_AXIS));
        pnlConfig.setPreferredSize(new Dimension(280, 0));
        pnlConfig.setBorder(
            BorderFactory.createTitledBorder("Configuración Genética")
        );

        spinSeed = new JSpinner(new SpinnerNumberModel(3000, 0, 100000, 1));
        spinPop = new JSpinner(new SpinnerNumberModel(100, 10, 2000, 10));
        spinGens = new JSpinner(new SpinnerNumberModel(100, 1, 5000, 10));
        spinCrossover = new JSpinner(new SpinnerNumberModel(60, 0, 100, 1));
        spinMutation = new JSpinner(new SpinnerNumberModel(15, 0, 100, 1));
        spinElitism = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
        spinBloat = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 10.0, 0.1));
        spinDepth = new JSpinner(new SpinnerNumberModel(6, 1, 20, 1));

        comboSelection = new JComboBox<>(
            new String[] { "Torneo", "Ruleta", "Ranking", "Restos" }
        );
        comboMutation = new JComboBox<>(
            new String[] {
                "Sub-árbol",
                "Aleatoria",
                "Funcional",
                "Terminal",
                "Hoist (Poda)",
            }
        );

        addSetting(pnlConfig, "Semilla (Random):", spinSeed);
        addSetting(pnlConfig, "Población:", spinPop);
        addSetting(pnlConfig, "Generaciones:", spinGens);
        addSetting(pnlConfig, "Prob. Cruce (%):", spinCrossover);
        addSetting(pnlConfig, "Prob. Mutación (%):", spinMutation);
        addSetting(pnlConfig, "Elitismo (%):", spinElitism);
        addSetting(pnlConfig, "Profundidad Max:", spinDepth);
        addSetting(pnlConfig, "Coef. Bloating:", spinBloat);
        addSetting(pnlConfig, "Método Selección:", comboSelection);
        addSetting(pnlConfig, "Tipo Mutación:", comboMutation);

        JButton btnRun = new JButton("EVOLUCIONAR GUSANOS");
        btnRun.setBackground(new Color(41, 128, 185));
        btnRun.setForeground(Color.WHITE);
        btnRun.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlConfig.add(Box.createVerticalStrut(20));
        pnlConfig.add(btnRun);

        btnSimulate = new JButton("SIMULAR MEJOR ESTRATEGIA");
        btnSimulate.setEnabled(false);
        btnSimulate.setBackground(new Color(46, 204, 113));
        btnSimulate.setForeground(Color.WHITE);
        btnSimulate.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlConfig.add(Box.createVerticalStrut(20));
        pnlConfig.add(btnSimulate);

        btnSimulate.addActionListener(e -> iniciarSimulacionVisual());

        // Cuando cambias la semilla manualmente, el mapa previo se actualiza
        spinSeed.addChangeListener((ChangeEvent e) -> actualizarMapaPrevio());

        // 2. Panel Central
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 10, 0));
        mapPanel = new MapPanel();
        graphPanel = new GraphPanel();
        pnlCenter.add(mapPanel);
        pnlCenter.add(graphPanel);

        // 3. Panel Inferior
        txtPhenotype = new JTextPane();
        txtPhenotype.setContentType("text/html");
        txtPhenotype.setEditable(false);
        txtPhenotype.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtPhenotype.setBackground(new Color(245, 245, 245));
        JScrollPane scrollCode = new JScrollPane(txtPhenotype);
        scrollCode.setPreferredSize(new Dimension(350, 250));
        scrollCode.setBorder(
            BorderFactory.createTitledBorder(
                "Estrategia Evolucionada (AST Lógico)"
            )
        );

        add(pnlConfig, BorderLayout.WEST);
        add(pnlCenter, BorderLayout.CENTER);
        add(scrollCode, BorderLayout.SOUTH);

        btnRun.addActionListener(e -> startEvolution());
        actualizarMapaPrevio();
    }

    private void addSetting(JPanel p, String label, JComponent comp) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(260, 45));
        row.add(new JLabel(label), BorderLayout.NORTH);
        row.add(comp, BorderLayout.CENTER);
        p.add(row);
        p.add(Box.createVerticalStrut(5));
    }

    /**
     * ¡CORREGIDO!: Ahora lee la semilla del spinner para mostrar el mapa real.
     */
    private void actualizarMapaPrevio() {
        long seed = ((Number) spinSeed.getValue()).longValue();
        Contexto ctxPrev = new Contexto(seed);
        mapPanel.updateView(ctxPrev.getEntorno(), ctxPrev.getAgente());
    }

    private void startEvolution() {
        graphPanel.reset();
        btnSimulate.setEnabled(false);
        txtPhenotype.setText("Calculando evolución...");

        long seed = ((Number) spinSeed.getValue()).longValue();
        int popSize = (int) spinPop.getValue();
        int generations = (int) spinGens.getValue();
        double crossRate = ((int) spinCrossover.getValue()) / 100.0;
        double mutRate = ((int) spinMutation.getValue()) / 100.0;
        double elitRate = ((int) spinElitism.getValue()) / 100.0;
        double bloatCoef = (double) spinBloat.getValue();
        int maxDepth = (int) spinDepth.getValue();

        String selectionType = (String) comboSelection.getSelectedItem();
        String mutationType = (String) comboMutation.getSelectedItem();

        new Thread(() -> {
            Algoritmo ag = new Algoritmo(
                popSize,
                generations,
                crossRate,
                mutRate,
                elitRate,
                maxDepth,
                bloatCoef,
                seed,
                selectionType,
                "Sub-árbol",
                mutationType
            );

            ag.setListener(
                new AlgoritmoListener() {
                    @Override
                    public void onGenerationCompleted(
                        int gen,
                        double best,
                        double avg,
                        double bestGlobal,
                        Individual bestInd,
                        java.util.List<Point> ruta
                    ) {
                        SwingUtilities.invokeLater(() -> {
                            graphPanel.updateData(
                                ag.getHistoricoMejores(),
                                ag.getHistoricoMedias(),
                                ag.getHistoricoMejoresGeneracion()
                            );

                            if (gen % 5 == 0) {
                                // Mostrar en el mapa cómo le va al mejor de la generación en el primer mapa del examen (seed)
                                mapPanel.updateView(
                                    ag.getEntornoMejor(),
                                    ag.getAgenteMejor()
                                );
                            }

                            setTitle(
                                "AI Lab Gusano - Generación: " +
                                    gen +
                                    " / " +
                                    generations
                            );
                        });
                    }

                    @Override
                    public void onAlgorithmFinished(
                        Individual bestInd,
                        java.util.List<Point> rutaMejorGlobal
                    ) {}
                }
            );

            ag.run();
            this.mejorGlobal = ag.getMejorGlobal();

            SwingUtilities.invokeLater(() -> {
                String htmlPhenotype =
                    "<html><pre style='font-family: monospace; font-size: 13px; margin: 10px;'>" +
                    mejorGlobal.getRaiz().toString(0) +
                    "</pre></html>";
                txtPhenotype.setText(htmlPhenotype);

                graphPanel.updateData(
                    ag.getHistoricoMejores(),
                    ag.getHistoricoMedias(),
                    ag.getHistoricoMejoresGeneracion()
                );
                actualizarMapaPrevio();
                btnSimulate.setEnabled(true);

                JOptionPane.showMessageDialog(
                    this,
                    "Evolución finalizada.\nMejor Fitness Global: " +
                        String.format("%.2f", mejorGlobal.getFitness())
                );
            });
        })
            .start();
    }

    /**
     * ¡CORREGIDO!: Pasa la semilla del examen para ver al gusano en acción
     */
    private void iniciarSimulacionVisual() {
        if (mejorGlobal == null) return;

        long seed = ((Number) spinSeed.getValue()).longValue();
        Contexto ctx = new Contexto(seed);
        Entorno entornoSim = ctx.getEntorno();
        Agente agenteSim = ctx.getAgente();

        btnSimulate.setEnabled(false);

        Timer timer = new Timer(50, null);
        timer.addActionListener(e -> {
            if (
                agenteSim.vivo && ctx.getEstadisticas().ticksSobrevividos < 500
            ) {
                mejorGlobal.execute(ctx);
                ctx.actualizarMetabolismo();
                mapPanel.updateView(entornoSim, agenteSim);
            } else {
                ((Timer) e.getSource()).stop();
                btnSimulate.setEnabled(true);
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
