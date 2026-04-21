package evolution.mutation;

import evolution.Individual;
import evolution.arboles.NodoAST;

public class HoistMutation implements MutationMethod {

    @Override
    public void mutate(Individual ind) {
        // Extraemos un nodo aleatorio. Puede ser una hoja o una rama entera.
        NodoAST nuevaRaiz = ind.extraerNodoAleatorio();

        if (nuevaRaiz != null) {
            // Reemplazamos el árbol completo por este sub-árbol
            ind.reemplazarNodo(ind.getRaiz(), nuevaRaiz.copy());
        }
    }
}
