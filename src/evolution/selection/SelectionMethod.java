package evolution.selection;

import evolution.*;
import java.util.ArrayList;

/**
 * Clase base para los métodos de selección (Ruleta, Torneo, Truncamiento, etc.)
 */
public abstract class SelectionMethod {

    protected String name;

    public SelectionMethod(String name) {
        this.name = name;
    }

    public abstract ArrayList<Individual> select(Population population);

    public String getName() {
        return name;
    }
}
