package evolution;

import java.util.ArrayList;
import java.util.List;

public class Population {

    private List<Individual> individuos;
    private int tamanoPoblacion;
    private int profMinima;
    private int profMaxima;

    private Individual mejorIndividuoGeneracion;
    private Individual mejorGlobal;
    private double fitnessMedio;

    public Population(int tamanoPoblacion, int profMinima, int profMaxima) {
        this.tamanoPoblacion = tamanoPoblacion;
        this.profMinima = profMinima;
        this.profMaxima = profMaxima;
        this.individuos = new ArrayList<>();
        this.mejorGlobal = null;
        inicializarRampedHalfAndHalf();
    }

    private void inicializarRampedHalfAndHalf() {
        this.individuos.clear();
        int nivelesDeProfundidad = profMaxima - profMinima + 1;
        int individuosPorNivel = tamanoPoblacion / nivelesDeProfundidad;

        for (
            int profundidad = profMinima;
            profundidad <= profMaxima;
            profundidad++
        ) {
            for (int i = 1; i <= individuosPorNivel; i++) {
                if (i <= individuosPorNivel / 2) {
                    this.individuos.add(
                        new Individual(
                            profundidad,
                            Individual.MetodoCreacion.FULL
                        )
                    );
                } else {
                    this.individuos.add(
                        new Individual(
                            profundidad,
                            Individual.MetodoCreacion.GROW
                        )
                    );
                }
            }
        }

        // Rellenamos si la división entera dejó huecos
        while (this.individuos.size() < tamanoPoblacion) {
            this.individuos.add(
                new Individual(profMaxima, Individual.MetodoCreacion.GROW)
            );
        }
    }

    // --- GESTIÓN DE POBLACIÓN Y ESTADÍSTICAS ---

    public void calcularEstadisticas() {
        if (individuos == null || individuos.isEmpty()) return;

        double suma = 0;
        Individual mejorActual = individuos.get(0);

        for (Individual ind : individuos) {
            suma += ind.getFitness();
            if (ind.getFitness() > mejorActual.getFitness()) {
                mejorActual = ind;
            }
        }

        // GUARDAR COPIA: Foto fija del mejor de esta generación
        this.mejorIndividuoGeneracion = mejorActual.copy();
        this.fitnessMedio = suma / tamanoPoblacion;

        // Actualizar Récord Histórico
        if (
            mejorGlobal == null ||
            mejorIndividuoGeneracion.getFitness() > mejorGlobal.getFitness()
        ) {
            mejorGlobal = mejorIndividuoGeneracion.copy();
        }
    }

    public void ordenarPoblacion() {
        // Orden descendente (mayor fitness primero)
        individuos.sort((ind1, ind2) ->
            Double.compare(ind2.getFitness(), ind1.getFitness())
        );
    }

    public List<Individual> extraerElite(int cantidadElite) {
        ordenarPoblacion();
        List<Individual> elite = new ArrayList<>();
        for (int i = 0; i < cantidadElite && i < individuos.size(); i++) {
            elite.add(individuos.get(i).copy());
        }
        return elite;
    }

    public void reinsertarElite(List<Individual> elite) {
        if (elite == null || elite.isEmpty()) return;

        ordenarPoblacion();

        // Reemplazamos a los peores individuos de la nueva generación con la élite
        int total = individuos.size();
        for (int i = 0; i < elite.size(); i++) {
            int targetIndex = total - 1 - i;
            if (targetIndex >= 0) {
                individuos.set(targetIndex, elite.get(i).copy());
            }
        }
    }

    // --- GETTERS ---

    public int getSize() {
        return individuos.size();
    }

    public List<Individual> getIndividuos() {
        return individuos;
    }

    public Individual getIndividual(int index) {
        return individuos.get(index);
    }

    public void setGeneracion(List<Individual> padres) {
        this.individuos = padres;
    }

    public Individual getMejorGeneracion() {
        return mejorIndividuoGeneracion;
    }

    public Individual getMejorGlobal() {
        return mejorGlobal;
    }

    public double getBestGlobalFitness() {
        return (mejorGlobal != null) ? mejorGlobal.getFitness() : 0.0;
    }

    public double getFitnessMedio() {
        return fitnessMedio;
    }
}
