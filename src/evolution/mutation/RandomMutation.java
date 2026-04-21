package evolution.mutation;

import evolution.Individual;
import java.util.Random;

public class RandomMutation implements MutationMethod {

    private Random random = new Random();
    private MutationMethod[] metodos;

    public RandomMutation(int maxDepth) {
        // Inicializamos nuestro arsenal de mutaciones
        this.metodos = new MutationMethod[] {
            new SubtreeMutation(maxDepth), // El único que necesita límite porque hace crecer ramas
            new HoistMutation(),
            new FunctionalMutation(),
            new TerminalMutation(),
        };
    }

    @Override
    public void mutate(Individual ind) {
        // Elegimos un método al azar y lo ejecutamos
        MutationMethod metodoElegido = metodos[random.nextInt(metodos.length)];
        metodoElegido.mutate(ind);
    }
}
