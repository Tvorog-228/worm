package evolution.fitness;

import evolution.Individual;
import evolution.entorno.Contexto;
import evolution.entorno.Estadisticas;

/**
 * Motor de evaluación para calcular el desempeño de un individuo (Gusano).
 * Somete al individuo a 3 pruebas en mapas aleatorios y puntúa su capacidad de supervivencia.
 */
public class FitnessCalculator {

    private long semillaBase;
    private double coefBloating;

    /**
     * @param coefBloating Multiplicador para penalizar el tamaño del árbol (ej: 0.1).
     * Evita que los gusanos generen árboles de 5000 líneas de código inútil.
     */
    public FitnessCalculator(long semillaBase, double coefBloating) {
        this.coefBloating = coefBloating;
        this.semillaBase = semillaBase;
    }

    /**
     * Calcula y asigna el fitness final del individuo.
     */
    public void calcular(Individual individual) {
        double fitnessTotalAcumulado = 0.0;

        // Evaluar al gusano en 3 vidas (mapas distintos) para asegurar que su lógica generaliza
        for (int i = 0; i < 3; i++) {
            Contexto ctx = new Contexto(this.semillaBase + i);

            // Bucle de vida: Termina si muere (por hambre/choques) o alcanza el límite de edad (500 ticks)
            while (
                ctx.getAgente().vivo &&
                ctx.getEstadisticas().ticksSobrevividos < 500
            ) {
                // 1. El Cerebro (AST) del gusano lee los sensores y decide una acción
                individual.execute(ctx);

                // 2. El motor físico procesa el desgaste de energía, sueño y recompensas pasivas
                ctx.actualizarMetabolismo();
            }

            // 3. Extraemos las estadísticas de esta vida y las puntuamos
            fitnessTotalAcumulado += puntuarVida(
                ctx.getEstadisticas(),
                ctx.getAgente().vivo
            );
        }

        // Se hace la media aritmética de las 3 vidas
        double fitnessMedio = fitnessTotalAcumulado / 3.0;

        // Impuesto al Bloating: Restamos puntos por cada línea de código (nodo) extra
        double impuestoBloating = individual.getNumNodos() * this.coefBloating;

        // Asignamos la nota final
        double fitnessFinal = fitnessMedio - impuestoBloating;
        individual.setFitness(fitnessFinal);
    }

    /**
     * Aplica la tabla de recompensas y castigos biológicos.
     */
    private double puntuarVida(Estadisticas stats, boolean sobrevivioAlMaximo) {
        double puntuacion = 0.0;

        // 1. EL GRAN PREMIO: Comer (+500 puntos por bocado)
        // Esto es lo que impulsa al gusano a buscar el color verde.
        puntuacion += stats.comidaIngerida * 500.0;

        // 2. Sobrevivir el máximo tiempo posible (+1 punto por tick vivo)
        puntuacion += stats.ticksSobrevividos * 1.0;

        // 3. Castigo por estupidez: Chocar contra muros (-50 puntos por choque)
        puntuacion -= stats.colisiones * 50.0;

        // 4. Bono de Edad Dorada: Si llega a los 500 ticks vivo, le damos un bonus extra
        if (sobrevivioAlMaximo) {
            puntuacion += 1000.0;
        }

        // 5. Castigo por Vagancia Absoluta (-2000 si no hace nada)

        if (stats.comidaIngerida == 0) {
            puntuacion -= 1500.0;
        }

        // 6. Premio sutil por descanso eficiente
        // Si logró dormir para recuperar sueño sin morir de hambre, le damos un pequeño premio
        puntuacion += stats.tiempoDormido * 0.5;

        return puntuacion;
    }
}
