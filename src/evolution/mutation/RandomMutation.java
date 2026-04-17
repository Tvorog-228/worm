package evolution.mutation;

import evolution.Individual;
import java.util.Random;

public class RandomMutation implements MutationMethod {

    private int maxDepth;

    public RandomMutation(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void mutate(Individual ind) {
        // Elige una mutación al azar de las anteriores
        Random r = new Random();
        int opc = r.nextInt(3);
        if (opc == 0) new TerminalMutation().mutate(ind);
        else if (opc == 1) new HoistMutation().mutate(ind);
        else new SubtreeMutation(maxDepth).mutate(ind);
    }
}
