package evolution.crossover;

import evolution.Individual;

public interface CrossoverMethod {
    Individual[] crossover(Individual padre1, Individual padre2);
}
