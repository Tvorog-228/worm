package evolution.crossover;

import evolution.Individual;
import evolution.arboles.NodoAST;

public class SubtreeCrossover implements CrossoverMethod {

    private int maxDepth; // Usamos el límite real de tu interfaz, no un 12 fijo

    // Constructor para recibir el límite desde Algoritmo.java
    public SubtreeCrossover(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public Individual[] crossover(Individual padre1, Individual padre2) {
        Individual hijo1 = padre1.copy();
        Individual hijo2 = padre2.copy();

        NodoAST punto1 = hijo1.extraerNodoSesgado();
        NodoAST punto2 = hijo2.extraerNodoSesgado();

        NodoAST copiaPunto1 = punto1.copy();
        NodoAST copiaPunto2 = punto2.copy();

        hijo1.reemplazarNodo(punto1, copiaPunto2);
        hijo2.reemplazarNodo(punto2, copiaPunto1);

        // --- ¡EL POLICÍA CORREGIDO! ---
        if (
            hijo1.getRaiz() == null ||
            hijo1.getNumNodos() > 200 ||
            hijo1.getDepth() > maxDepth
        ) {
            hijo1 = padre1.copy();
        }
        if (
            hijo2.getRaiz() == null ||
            hijo2.getNumNodos() > 200 ||
            hijo2.getDepth() > maxDepth
        ) {
            hijo2 = padre2.copy();
        }

        return new Individual[] { hijo1, hijo2 };
    }
}
