package logic;

import evolution.Individual;
import java.awt.Point;
import java.util.List;

public interface AlgoritmoListener {
    void onGenerationCompleted(
        int gen,
        double best,
        double avg,
        double bestGlobal,
        Individual bestInd,
        List<Point> ruta
    );
    void onAlgorithmFinished(Individual bestInd, List<Point> rutaMejorGlobal);
}
