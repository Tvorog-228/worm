package evolution.mutation;

import evolution.Individual;
import evolution.arboles.NodoAST;
import evolution.arboles.NodoCondicional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FunctionalMutation implements MutationMethod {

    private Random random = new Random();

    @Override
    public void mutate(Individual ind) {
        if (ind.getRaiz() == null) return;

        // Recolectamos solo los nodos de tipo "IF" (Condicionales)
        List<NodoAST> funcionales = new ArrayList<>();
        recolectarFuncionales(ind.getRaiz(), funcionales);

        if (funcionales.isEmpty()) return;

        // Elegimos un IF al azar para mutarlo
        NodoCondicional target = (NodoCondicional) funcionales.get(
            random.nextInt(funcionales.size())
        );

        // Generamos nuevos valores aleatorios para la condición
        NodoCondicional.AtributoSensor sensor =
            NodoCondicional.AtributoSensor.values()[random.nextInt(
                NodoCondicional.AtributoSensor.values().length
            )];
        NodoCondicional.Operador operador =
            NodoCondicional.Operador.values()[random.nextInt(
                NodoCondicional.Operador.values().length
            )];
        double umbral = NodoCondicional
            .UMBRALES[random.nextInt(NodoCondicional.UMBRALES.length)];
        int distanciaFocal = random.nextInt(15) + 1;

        // Creamos la nueva condición
        NodoCondicional nuevoNodo = new NodoCondicional(
            sensor,
            operador,
            umbral,
            distanciaFocal
        );

        // ¡LA MAGIA! Le pegamos los hijos (ramas) del IF antiguo al IF nuevo
        List<NodoAST> hijos = target.getHijos();
        if (hijos != null && hijos.size() >= 2) {
            nuevoNodo.setHijoIzquierdo(hijos.get(0).copy());
            nuevoNodo.setHijoDerecho(hijos.get(1).copy());
        }

        // Reemplazamos en el árbol
        ind.reemplazarNodo(target, nuevoNodo);
    }

    // Función recursiva auxiliar para encontrar los IFs
    private void recolectarFuncionales(NodoAST nodo, List<NodoAST> lista) {
        if (nodo instanceof NodoCondicional) {
            lista.add(nodo);
        }
        if (nodo.getHijos() != null) {
            for (NodoAST hijo : nodo.getHijos()) {
                recolectarFuncionales(hijo, lista);
            }
        }
    }
}
