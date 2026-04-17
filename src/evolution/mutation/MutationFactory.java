package evolution.mutation;

public class MutationFactory {

    public static MutationMethod getMethod(String name, int maxDepth) {
        switch (name) {
            case "Sub-árbol":
                return new SubtreeMutation(maxDepth);
            case "Hoist (Poda)":
                return new HoistMutation();
            case "Funcional":
                return new FunctionalMutation();
            case "Terminal":
                return new TerminalMutation();
            case "Aleatoria":
                return new RandomMutation(maxDepth);
            default:
                return new TerminalMutation();
        }
    }
}
