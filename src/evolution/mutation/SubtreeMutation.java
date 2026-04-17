package evolution.mutation;

import evolution.Individual;
import evolution.arboles.NodoAST;

public class SubtreeMutation implements MutationMethod {

    private int maxDepth;
    private static final int MAX_NODOS_PERMITIDOS = 300;

    public SubtreeMutation(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void mutate(Individual ind) {
        Individual copiaSeguridad = ind.copy();

        NodoAST puntoMutacion = ind.extraerNodoAleatorio();
        if (puntoMutacion == null) return;

        int profundidadRama = Math.min(3, maxDepth);
        NodoAST nuevaRama = ind.generarNuevaRama(profundidadRama);

        ind.reemplazarNodo(puntoMutacion, nuevaRama);

        // --- ¡EL POLICÍA CORREGIDO! ---
        // Ahora vigila los nodos Y la profundidad máxima de la interfaz
        if (
            ind.getRaiz() == null ||
            ind.getNumNodos() > MAX_NODOS_PERMITIDOS ||
            ind.getDepth() > maxDepth
        ) {
            ind.reemplazarNodo(ind.getRaiz(), copiaSeguridad.getRaiz());
        }
    }
}
