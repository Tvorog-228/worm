package evolution.selection;

public class SelectionFactory {

    public static SelectionMethod getMethod(String name) {
        switch (name) {
            case "Torneo":
                return new TorneoDeterminista();
            default:
                return new TorneoDeterminista();
        }
    }
}
