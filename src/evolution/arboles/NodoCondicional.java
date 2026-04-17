package evolution.arboles;

import evolution.entorno.Contexto;
import evolution.entorno.Entorno; // Importamos Entorno para los límites
import java.util.ArrayList;
import java.util.List;

public class NodoCondicional implements NodoAST {

    public enum AtributoSensor {
        OBSTACULO_N,
        OBSTACULO_S,
        OBSTACULO_E,
        OBSTACULO_O,
        COMIDA_N,
        COMIDA_S,
        COMIDA_E,
        COMIDA_O,
        DESCANSO_N,
        DESCANSO_S,
        DESCANSO_E,
        DESCANSO_O,
        ENERGIA,
        SUENO,
    }

    public enum Operador {
        MENOR,
        IGUAL,
        MAYOR,
    }

    public static final double[] UMBRALES = { 0.1, 0.5, 0.9, 25.0, 50.0, 75.0 };

    private AtributoSensor sensor;
    private Operador operador;
    private double umbral;

    // NUEVO GEN: A qué distancia mira este sensor
    private int distancia;

    private NodoAST hijoIzquierdo;
    private NodoAST hijoDerecho;

    // Constructor actualizado para incluir la distancia
    public NodoCondicional(
        AtributoSensor sensor,
        Operador operador,
        double umbral,
        int distancia
    ) {
        this.sensor = sensor;
        this.operador = operador;
        this.umbral = umbral;
        this.distancia = distancia;
    }

    @Override
    public void ejecutar(Contexto ctx) {
        // Le pasamos la distancia al contexto para que lea a lo lejos
        double valorSensor = ctx.leerSensor(this.sensor, this.distancia);
        boolean condicionCumplida = false;

        switch (this.operador) {
            case MENOR:
                condicionCumplida = valorSensor < this.umbral;
                break;
            case MAYOR:
                condicionCumplida = valorSensor > this.umbral;
                break;
            case IGUAL:
                condicionCumplida = Math.abs(valorSensor - this.umbral) < 0.01;
                break;
        }

        if (condicionCumplida) {
            if (hijoIzquierdo != null) hijoIzquierdo.ejecutar(ctx);
        } else {
            if (hijoDerecho != null) hijoDerecho.ejecutar(ctx);
        }
    }

    public void setHijoIzquierdo(NodoAST hijo) {
        this.hijoIzquierdo = hijo;
    }

    public void setHijoDerecho(NodoAST hijo) {
        this.hijoDerecho = hijo;
    }

    @Override
    public NodoAST copy() {
        // La copia ahora respeta la distancia mutada
        NodoCondicional copia = new NodoCondicional(
            this.sensor,
            this.operador,
            this.umbral,
            this.distancia
        );
        if (this.hijoIzquierdo != null) copia.setHijoIzquierdo(
            this.hijoIzquierdo.copy()
        );
        if (this.hijoDerecho != null) copia.setHijoDerecho(
            this.hijoDerecho.copy()
        );
        return copia;
    }

    @Override
    public int getNumeroNodos() {
        int nodosIzq = (this.hijoIzquierdo != null)
            ? this.hijoIzquierdo.getNumeroNodos()
            : 0;
        int nodosDerch = (this.hijoDerecho != null)
            ? this.hijoDerecho.getNumeroNodos()
            : 0;
        return 1 + nodosIzq + nodosDerch;
    }

    @Override
    public List<NodoAST> getHijos() {
        List<NodoAST> hijos = new ArrayList<>();
        hijos.add(hijoIzquierdo);
        hijos.add(hijoDerecho);
        return hijos;
    }

    @Override
    public void setHijo(int index, NodoAST nodo) {
        if (index == 0) this.hijoIzquierdo = nodo;
        else if (index == 1) this.hijoDerecho = nodo;
    }

    @Override
    public String toString(int nivel) {
        String tab = "\t".repeat(nivel);
        StringBuilder sb = new StringBuilder();

        String simboloOp = "";
        switch (this.operador) {
            case MENOR:
                simboloOp = "&lt;";
                break;
            case MAYOR:
                simboloOp = "&gt;";
                break;
            case IGUAL:
                simboloOp = "==";
                break;
        }

        // Modificamos cómo se imprime para que muestre el alcance visual (ej: COMIDA_N [Dist: 3])
        String textoSensor = this.sensor.name();
        if (!textoSensor.equals("ENERGIA") && !textoSensor.equals("SUENO")) {
            textoSensor += " [Dist: " + this.distancia + "]";
        }

        sb
            .append(tab)
            .append("<font color='#800080'><b>IF</b></font> (")
            .append("<font color='#d35400'>")
            .append(textoSensor)
            .append("</font> ")
            .append("<b>")
            .append(simboloOp)
            .append("</b> ")
            .append("<font color='#27ae60'>")
            .append(this.umbral)
            .append("</font>")
            .append(") {\n");

        if (hijoIzquierdo != null) sb
            .append(hijoIzquierdo.toString(nivel + 1))
            .append("\n");
        sb.append(tab).append("} <font color='#800080'><b>ELSE</b></font> {\n");
        if (hijoDerecho != null) sb
            .append(hijoDerecho.toString(nivel + 1))
            .append("\n");
        sb.append(tab).append("}");

        return sb.toString();
    }
}
