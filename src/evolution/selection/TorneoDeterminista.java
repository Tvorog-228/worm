package evolution.selection;

import evolution.Individual;
import evolution.Population;
import java.util.ArrayList;
import java.util.Random;

public class TorneoDeterminista extends SelectionMethod {

    private final int k = 3; // Tamaño del torneo

    public TorneoDeterminista() {
        super("Torneo");
    }

    @Override
    public ArrayList<Individual> select(Population population) {
        ArrayList<Individual> selected = new ArrayList<>();
        ArrayList<Individual> individuals = (ArrayList<
            Individual
        >) population.getIndividuos();
        int popSize = population.getSize();
        Random rand = new Random();

        // Necesitamos seleccionar tantos individuos como tamaño tenga la población
        for (int i = 0; i < popSize; i++) {
            Individual bestInTournament = null;

            // Realizamos un torneo de tamaño k
            for (int j = 0; j < k; j++) {
                Individual candidate = individuals.get(rand.nextInt(popSize));

                if (
                    bestInTournament == null ||
                    candidate.getFitness() > bestInTournament.getFitness()
                ) {
                    bestInTournament = candidate;
                }
            }

            // Añadimos una copia del ganador para evitar problemas de referencias
            selected.add(bestInTournament.copy());
        }

        return selected;
    }
}
