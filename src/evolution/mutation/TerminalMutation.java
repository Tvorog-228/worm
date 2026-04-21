package evolution.mutation;

import evolution.Individual;
import evolution.arboles.NodoAST;
import evolution.arboles.NodoAccion;
import java.util.List;
import java.util.Random;

public class TerminalMutation implements MutationMethod {

    private Random random = new Random();

    @Override
    public void mutate(Individual ind) {
        if (ind.getRaiz() == null) return;

        // Obtenemos solo las hojas usando el método que arreglamos en NodoAST
        List<NodoAST> terminales = ind.getRaiz().getTerminalNodes();
        if (terminales.isEmpty()) return;

        // Elegimos una hoja al azar
        NodoAST target = terminales.get(random.nextInt(terminales.size()));

        if (target instanceof NodoAccion) {
            // Elegir una nueva acción aleatoria
            NodoAccion.AtributoAccion[] acciones =
                NodoAccion.AtributoAccion.values();
            NodoAccion.AtributoAccion nuevaAccion = acciones[random.nextInt(
                acciones.length
            )];

            // Creamos el nodo nuevo y lo intercambiamos
            NodoAST nuevoNodo = new NodoAccion(nuevaAccion);
            ind.reemplazarNodo(target, nuevoNodo);
        }
    }
}
