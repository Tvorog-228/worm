package evolution.crossover;

public class CrossoverFactory {

    // Añadimos int maxDepth a los parámetros
    public static CrossoverMethod getMethod(String name, int maxDepth) {
        // En esta práctica el estándar es Sub-árbol
        // Le pasamos el límite al constructor para controlar el Bloating
        return new SubtreeCrossover(maxDepth);
    }
}
