package evolution.arboles;

import evolution.entorno.Contexto;
import java.util.ArrayList;
import java.util.List;

public interface NodoAST {
    void ejecutar(Contexto ctx);
    int getNumeroNodos();
    NodoAST copy();
    String toString(int nivel);

    List<NodoAST> getHijos();
    void setHijo(int index, NodoAST nodo);

    default List<NodoAST> getTerminalNodes() {
        List<NodoAST> terminals = new ArrayList<>();
        collectTerminals(this, terminals);
        return terminals;
    }

    default int getProfundidad() {
        List<NodoAST> hijos = getHijos();
        if (hijos == null || hijos.isEmpty()) {
            return 0; // Una hoja sola no tiene profundidad
        }

        int maxHijo = 0;
        for (NodoAST hijo : hijos) {
            maxHijo = Math.max(maxHijo, hijo.getProfundidad());
        }
        return 1 + maxHijo; // Sumamos 1 por el nivel actual
    }

    private void collectTerminals(NodoAST node, List<NodoAST> terminals) {
        List<NodoAST> hijos = node.getHijos();

        if (hijos == null || hijos.isEmpty()) {
            terminals.add(node);
        } else {
            for (NodoAST hijo : hijos) {
                collectTerminals(hijo, terminals);
            }
        }
    }
}
