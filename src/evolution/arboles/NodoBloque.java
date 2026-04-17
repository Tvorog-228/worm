package evolution.arboles;

import evolution.entorno.Contexto;
import java.util.LinkedList;
import java.util.List;

public class NodoBloque implements NodoAST {

    private List<NodoAST> hijos;

    public NodoBloque() {
        this.hijos = new LinkedList<>();
    }

    @Override
    public void ejecutar(Contexto ctx) {
        for (NodoAST hijo : hijos) hijo.ejecutar(ctx);
    }

    public void addHijo(NodoAST hijo) {
        this.hijos.add(hijo);
    }

    @Override
    public NodoAST copy() {
        NodoBloque copia = new NodoBloque();
        for (NodoAST hijo : this.hijos) {
            copia.addHijo(hijo.copy());
        }
        return copia;
    }

    @Override
    public int getNumeroNodos() {
        int n = 1;

        for (NodoAST nodo : hijos) n += nodo.getNumeroNodos();

        return n;
    }

    @Override
    public List<NodoAST> getHijos() {
        return this.hijos;
    }

    @Override
    public void setHijo(int index, NodoAST nodo) {
        if (index >= 0 && index < hijos.size()) {
            hijos.set(index, nodo);
        }
    }

    @Override
    public String toString(int nivel) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < hijos.size(); i++) {
            sb.append(hijos.get(i).toString(nivel));

            // Añadimos salto de línea entre sentencias, excepto en la última
            if (i < hijos.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
