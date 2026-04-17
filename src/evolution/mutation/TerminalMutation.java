package evolution.mutation;

import evolution.Individual;
import evolution.arboles.NodoAST;
import evolution.arboles.NodoAccion;
import java.util.Random;

public class TerminalMutation implements MutationMethod {

    @Override
    public void mutate(Individual ind) {
        // Obtenemos todos los nodos hoja (acciones)
        java.util.List<NodoAST> hojas = ind.getRaiz().getTerminalNodes();

        if (!hojas.isEmpty()) {
            Random rand = new Random();
            // Elegimos una hoja al azar
            NodoAST nodoParaCambiar = hojas.get(rand.nextInt(hojas.size()));

            if (nodoParaCambiar instanceof NodoAccion) {
                NodoAccion accion = (NodoAccion) nodoParaCambiar;
                // Cambiamos su acción por una nueva aleatoria
                NodoAccion.AtributoAccion[] opciones =
                    NodoAccion.AtributoAccion.values();
                accion.setAccion(opciones[rand.nextInt(opciones.length)]);
            }
        }
    }
}
