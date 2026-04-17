package evolution.arboles;

import evolution.entorno.Contexto;
import java.util.ArrayList;
import java.util.List;

public class NodoAccion implements NodoAST {

    // Las 5 acciones posibles del gusano
    public enum AtributoAccion {
        QUIETO,
        NORTE,
        SUR,
        ESTE,
        OESTE,
    }

    private AtributoAccion tipoAccion;

    public NodoAccion(AtributoAccion accion) {
        this.tipoAccion = accion;
    }

    @Override
    public void ejecutar(Contexto ctx) {
        // Ejecutamos usando el ordinal (0 a 4) que encaja con el Enum de Contexto
        ctx.ejecutarAccion(this.tipoAccion.ordinal());
    }

    @Override
    public NodoAST copy() {
        return new NodoAccion(this.tipoAccion);
    }

    @Override
    public int getNumeroNodos() {
        return 1;
    }

    @Override
    public String toString(int nivel) {
        String tab = "\t".repeat(nivel);

        // Añadimos un toque visual para Swing
        String icono = "";
        switch (this.tipoAccion) {
            case NORTE:
                icono = "⬆️";
                break;
            case SUR:
                icono = "⬇️";
                break;
            case ESTE:
                icono = "➡️";
                break;
            case OESTE:
                icono = "⬅️";
                break;
            case QUIETO:
                icono = "💤";
                break;
        }

        return (
            tab +
            "<font color='#0066cc'><b>" +
            this.tipoAccion.name() +
            " " +
            icono +
            "</b></font>;"
        );
    }

    @Override
    public List<NodoAST> getHijos() {
        return new ArrayList<>(); // Es hoja
    }

    @Override
    public void setHijo(int index, NodoAST nodo) {}

    public void setAccion(AtributoAccion nuevaAccion) {
        this.tipoAccion = nuevaAccion;
    }
}
