package logic;

import evolution.Individual;
import evolution.Population;
import evolution.crossover.CrossoverFactory;
import evolution.crossover.CrossoverMethod;
import evolution.entorno.Agente;
import evolution.entorno.Contexto;
import evolution.entorno.Entorno;
import evolution.fitness.FitnessCalculator;
import evolution.mutation.MutationFactory;
import evolution.mutation.MutationMethod;
import evolution.selection.SelectionFactory;
import evolution.selection.SelectionMethod;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Algoritmo {

    private int populationSize;
    private int numGenerations;
    private double crossoverRate;
    private double mutationRate;
    private double elitismRate;
    private int maxDepth;

    @SuppressWarnings("unused")
    private double bloatingCoef;

    private long seed;

    private Population poblacion;
    private FitnessCalculator fitnessCalculator;
    private AlgoritmoListener listener;
    private Random random;

    private SelectionMethod selectionMethod;
    private CrossoverMethod crossoverMethod;
    private MutationMethod mutationMethod;

    // Historiales para la gráfica
    private List<Double> historicoMejores = new ArrayList<>(); // Azul: Mejor Global
    private List<Double> historicoMedias = new ArrayList<>(); // Verde: Media
    private List<Double> historicoMejoresGeneracion = new ArrayList<>(); // Roja: Mejor Gen. Actual

    private Contexto contextoMejorFinal;

    public Algoritmo(
        int populationSize,
        int numGenerations,
        double crossoverRate,
        double mutationRate,
        double elitismRate,
        int maxDepth,
        double bloatingCoef,
        long seed,
        String selectionMethod,
        String crossoverMethod,
        String mutationMethod
    ) {
        this.populationSize = populationSize;
        this.numGenerations = numGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismRate = elitismRate;
        this.maxDepth = maxDepth;
        this.bloatingCoef = bloatingCoef;
        this.seed = seed;
        this.listener = null;

        this.random = new Random();
        this.fitnessCalculator = new FitnessCalculator(seed, bloatingCoef);
        this.poblacion = null;

        // Inicialización de operadores mediante factorías
        this.selectionMethod = SelectionFactory.getMethod(selectionMethod);
        this.crossoverMethod = CrossoverFactory.getMethod(
            crossoverMethod,
            maxDepth
        );
        this.mutationMethod = MutationFactory.getMethod(
            mutationMethod,
            maxDepth
        );
    }

    public void setListener(AlgoritmoListener listener) {
        this.listener = listener;
    }

    public void run() {
        // Limpiamos datos de ejecuciones anteriores
        historicoMejores.clear();
        historicoMedias.clear();
        historicoMejoresGeneracion.clear();

        // Inicialización de la población (Generación 0)
        this.poblacion = new Population(populationSize, 1, maxDepth);
        evaluarPoblacion();

        for (int gen = 0; gen < numGenerations; gen++) {
            evolucionarGeneracion();

            // Captura de estadísticas de la generación actual
            Individual mejorGen = poblacion.getMejorGeneracion();

            historicoMejoresGeneracion.add(mejorGen.getFitness()); // Línea Roja
            historicoMejores.add(poblacion.getBestGlobalFitness()); // Línea Azul
            historicoMedias.add(poblacion.getFitnessMedio()); // Línea Verde

            if (listener != null) {
                listener.onGenerationCompleted(
                    gen,
                    mejorGen.getFitness(),
                    poblacion.getFitnessMedio(),
                    poblacion.getBestGlobalFitness(),
                    mejorGen,
                    obtenerRutaVisualizacion(mejorGen)
                );
            }
        }

        prepararSimulacionFinal();

        if (listener != null) {
            listener.onAlgorithmFinished(
                poblacion.getMejorGlobal(),
                obtenerRutaVisualizacion(poblacion.getMejorGlobal())
            );
        }
    }

    private void evaluarPoblacion() {
        for (Individual ind : poblacion.getIndividuos()) {
            fitnessCalculator.calcular(ind);
        }
        poblacion.calcularEstadisticas();
    }

    private void evolucionarGeneracion() {
        // 1. Elitismo
        int numElite = (int) (populationSize * elitismRate);
        List<Individual> elite = poblacion.extraerElite(numElite);

        // 2. Selección de padres
        List<Individual> padres = selectionMethod.select(poblacion);

        // 3. Cruce y Mutación
        crucePoblacion(padres);
        mutarPoblacion(padres);

        // 4. Reemplazo y Reinserción de élite
        poblacion.setGeneracion(padres);
        evaluarPoblacion();
        poblacion.reinsertarElite(elite);
    }

    private void crucePoblacion(List<Individual> poblacionHijos) {
        for (int i = 0; i < poblacionHijos.size() - 1; i += 2) {
            if (random.nextDouble() < crossoverRate) {
                // Guardamos copias de los padres por si los hijos salen "mutantes gigantes"
                Individual padre1 = poblacionHijos.get(i);
                Individual padre2 = poblacionHijos.get(i + 1);

                Individual[] hijos = crossoverMethod.crossover(padre1, padre2);

                // --- EL FILTRO DE SEGURIDAD ---
                // Solo aceptamos al hijo 0 si no supera el techo de cristal
                if (hijos[0].getDepth() <= maxDepth) {
                    poblacionHijos.set(i, hijos[0]);
                } else {
                    // Si es demasiado profundo, mantenemos al padre original
                    poblacionHijos.set(i, padre1);
                }

                // Lo mismo para el hijo 1
                if (hijos[1].getDepth() <= maxDepth) {
                    poblacionHijos.set(i + 1, hijos[1]);
                } else {
                    poblacionHijos.set(i + 1, padre2);
                }
            }
        }
    }

    private void mutarPoblacion(List<Individual> poblacionHijos) {
        for (Individual ind : poblacionHijos) {
            if (random.nextDouble() < mutationRate) {
                mutationMethod.mutate(ind);
            }
        }
    }

    private List<Point> obtenerRutaVisualizacion(Individual ind) {
        Contexto ctx = new Contexto(this.seed);
        int ticks = 0;
        while (ctx.getAgente().vivo && ticks < 500) {
            ind.execute(ctx);
            ctx.actualizarMetabolismo();
            ticks++;
        }
        return ctx.getEntorno().getRuta();
    }

    private void prepararSimulacionFinal() {
        this.contextoMejorFinal = new Contexto(this.seed);
        Individual mejor = poblacion.getMejorGlobal();
        int ticks = 0;
        while (contextoMejorFinal.getAgente().vivo && ticks < 500) {
            mejor.execute(contextoMejorFinal);
            contextoMejorFinal.actualizarMetabolismo();
            ticks++;
        }
    }

    // --- Getters para la GUI ---

    public Individual getMejorGlobal() {
        return poblacion.getMejorGlobal();
    }

    public List<Double> getHistoricoMejores() {
        return historicoMejores;
    }

    public List<Double> getHistoricoMedias() {
        return historicoMedias;
    }

    public List<Double> getHistoricoMejoresGeneracion() {
        return historicoMejoresGeneracion;
    }

    public Entorno getEntornoMejor() {
        return contextoMejorFinal != null
            ? contextoMejorFinal.getEntorno()
            : null;
    }

    public Agente getAgenteMejor() {
        return contextoMejorFinal != null
            ? contextoMejorFinal.getAgente()
            : null;
    }
}
