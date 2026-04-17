package evolution;

import evolution.arboles.*;
import evolution.entorno.Contexto;
import evolution.entorno.Entorno;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Individual {

    public enum MetodoCreacion {
        FULL,
        GROW,
    }

    private NodoAST raiz;
    private double fitness;
    private Random random;

    private boolean primeraAccionGenerada;

    public Individual(int profundidadMax, MetodoCreacion metodo) {
        this.random = new Random();
        this.fitness = 0.0;
        this.primeraAccionGenerada = false;

        if (metodo == MetodoCreacion.FULL) {
            this.raiz = generarArbolFull(0, profundidadMax);
        } else {
            this.raiz = generarArbolGrow(0, profundidadMax);
        }
    }

    // Constructor privado para copias
    private Individual() {
        this.random = new Random();
        this.fitness = 0.0;
        this.raiz = null;
        this.primeraAccionGenerada = true;
    }

    public void execute(Contexto ctx) {
        if (raiz != null) {
            raiz.ejecutar(ctx);
        }
    }

    // --- MANEJO DE NODOS PARA MUTACIÓN Y CRUCE ---

    public NodoAST extraerNodoAleatorio() {
        List<NodoAST> todos = obtenerTodosLosNodos(this.raiz);
        if (todos.isEmpty()) return null;
        return todos.get(random.nextInt(todos.size()));
    }

    public NodoAST extraerNodoSesgado() {
        List<NodoAST> todos = obtenerTodosLosNodos(this.raiz);
        if (todos.isEmpty()) return null;

        List<NodoBloque> bloques = new ArrayList<>();
        for (NodoAST n : todos) {
            if (n instanceof NodoBloque) bloques.add((NodoBloque) n);
        }

        if (!bloques.isEmpty() && random.nextDouble() < 0.70) {
            return bloques.get(random.nextInt(bloques.size()));
        }
        return todos.get(random.nextInt(todos.size()));
    }

    public void reemplazarNodo(NodoAST viejo, NodoAST nuevo) {
        if (this.raiz == viejo) {
            this.raiz = nuevo.copy();
        } else {
            buscarYReemplazar(this.raiz, viejo, nuevo);
        }
    }

    private List<NodoAST> obtenerTodosLosNodos(NodoAST nodo) {
        List<NodoAST> lista = new ArrayList<>();
        lista.add(nodo);
        for (NodoAST hijo : nodo.getHijos()) {
            lista.addAll(obtenerTodosLosNodos(hijo));
        }
        return lista;
    }

    private boolean buscarYReemplazar(
        NodoAST actual,
        NodoAST viejo,
        NodoAST nuevo
    ) {
        List<NodoAST> hijos = actual.getHijos();
        for (int i = 0; i < hijos.size(); i++) {
            if (hijos.get(i) == viejo) {
                actual.setHijo(i, nuevo.copy());
                return true;
            }
            if (buscarYReemplazar(hijos.get(i), viejo, nuevo)) return true;
        }
        return false;
    }

    public NodoAST generarNuevaRama(int profMax) {
        return generarArbolGrow(0, profMax);
    }

    // --- GENERACIÓN DE AST (FABRICA) ---

    private NodoAST generarArbolFull(int prof, int max) {
        if (prof >= max - 1) return generarNodoAccion();

        if (random.nextDouble() < 0.7) {
            return generarNodoCondicional(prof, max, true);
        } else {
            return generarNodoBloque(prof, max, true);
        }
    }

    private NodoAST generarArbolGrow(int prof, int max) {
        if (prof >= max - 1) return generarNodoAccion();

        if (random.nextDouble() < 0.40) return generarNodoAccion();

        if (random.nextDouble() < 0.7) {
            return generarNodoCondicional(prof, max, false);
        } else {
            return generarNodoBloque(prof, max, false);
        }
    }

    private NodoCondicional generarNodoCondicional(
        int prof,
        int max,
        boolean isFull
    ) {
        NodoCondicional.AtributoSensor[] sensores =
            NodoCondicional.AtributoSensor.values();
        NodoCondicional.AtributoSensor sensor = sensores[random.nextInt(
            sensores.length
        )];

        NodoCondicional.Operador[] operadores =
            NodoCondicional.Operador.values();
        NodoCondicional.Operador operador = operadores[random.nextInt(
            operadores.length
        )];

        double umbral = NodoCondicional
            .UMBRALES[random.nextInt(NodoCondicional.UMBRALES.length)];

        int distanciaFocal = random.nextInt(15) + 1;

        NodoCondicional nodo = new NodoCondicional(
            sensor,
            operador,
            umbral,
            distanciaFocal
        );

        if (isFull) {
            nodo.setHijoIzquierdo(generarArbolFull(prof + 1, max));
            nodo.setHijoDerecho(generarArbolFull(prof + 1, max));
        } else {
            nodo.setHijoIzquierdo(generarArbolGrow(prof + 1, max));
            nodo.setHijoDerecho(generarArbolGrow(prof + 1, max));
        }
        return nodo;
    }

    private NodoBloque generarNodoBloque(int prof, int max, boolean isFull) {
        NodoBloque nodo = new NodoBloque();
        int numHijos = 2 + random.nextInt(2);
        for (int i = 0; i < numHijos; i++) {
            nodo.addHijo(
                isFull
                    ? generarArbolFull(prof + 1, max)
                    : generarArbolGrow(prof + 1, max)
            );
        }
        return nodo;
    }

    private NodoAccion generarNodoAccion() {
        NodoAccion.AtributoAccion[] acciones =
            NodoAccion.AtributoAccion.values();
        List<NodoAccion.AtributoAccion> candidatos = new ArrayList<>(
            Arrays.asList(acciones)
        );

        if (!this.primeraAccionGenerada) {
            candidatos.add(NodoAccion.AtributoAccion.NORTE);
            candidatos.add(NodoAccion.AtributoAccion.ESTE);
            this.primeraAccionGenerada = true;
        }

        return new NodoAccion(
            candidatos.get(random.nextInt(candidatos.size()))
        );
    }

    // --- GETTERS & SETTERS ---

    public NodoAST getRaiz() {
        return this.raiz;
    }

    public int getNumNodos() {
        return this.raiz != null ? this.raiz.getNumeroNodos() : 0;
    }

    /**
     * Devuelve la profundidad máxima actual del árbol.
     * Requiere que NodoAST tenga implementado getProfundidad().
     */
    public int getDepth() {
        return (this.raiz != null) ? this.raiz.getProfundidad() : 0;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Individual copy() {
        Individual copia = new Individual();
        copia.fitness = this.fitness;
        if (this.raiz != null) {
            copia.raiz = this.raiz.copy();
        }
        return copia;
    }
}
