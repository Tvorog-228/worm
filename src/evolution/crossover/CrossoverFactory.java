package evolution.crossover;

public class CrossoverFactory {

    public static CrossoverMethod getMethod(String name, int maxDepth) {
        return new SubtreeCrossover(maxDepth);
    }
}
