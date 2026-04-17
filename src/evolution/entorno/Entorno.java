package evolution.entorno;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Entorno {

    public static final int ANCHO = 40;
    public static final int ALTO = 25;

    public static final int SUELO = 0;
    public static final int MURO = 1;
    public static final int COMIDA = 2;
    public static final int DESCANSO = 3;

    private int[][] mapa;
    private boolean[][] visitado;
    private List<Point> ruta;
    private Random rand;

    public Entorno(long semilla) {
        this.mapa = new int[ALTO][ANCHO];
        this.visitado = new boolean[ALTO][ANCHO];
        this.ruta = new ArrayList<>();
        // No usamos semilla fija, queremos que el mapa cambie en cada ejecución
        this.rand = new Random(semilla);
        generarEscenario();
    }

    private void generarEscenario() {
        for (int y = 0; y < ALTO; y++) {
            for (int x = 0; x < ANCHO; x++) {
                // Muros perimetrales
                if (x == 0 || x == ANCHO - 1 || y == 0 || y == ALTO - 1) {
                    mapa[y][x] = MURO;
                } else {
                    mapa[y][x] = SUELO;
                }
            }
        }

        // Obstáculo central aleatorio
        int ox = rand.nextInt(ANCHO / 2) + 5;
        int oy = rand.nextInt(ALTO / 2) + 5;
        for (int y = oy; y < oy + 5; y++) {
            for (int x = ox; x < ox + 10; x++) {
                if (estaEnLimites(x, y)) mapa[y][x] = MURO;
            }
        }

        // Zonas de Descanso dispersas
        for (int i = 0; i < 3; i++) {
            int dx = rand.nextInt(ANCHO - 6) + 3;
            int dy = rand.nextInt(ALTO - 6) + 3;
            for (int rY = dy; rY < dy + 4; rY++) {
                for (int rX = dx; rX < dx + 4; rX++) {
                    if (estaEnLimites(rX, rY) && mapa[rY][rX] == SUELO) {
                        mapa[rY][rX] = DESCANSO;
                    }
                }
            }
        }

        // Aparecer comida inicial
        for (int i = 0; i < 20; i++) {
            spawnComida();
        }
    }

    public void spawnComida() {
        int intentos = 0;
        while (intentos < 100) {
            int rx = rand.nextInt(ANCHO - 2) + 1;
            int ry = rand.nextInt(ALTO - 2) + 1;
            if (mapa[ry][rx] == SUELO) {
                mapa[ry][rx] = COMIDA;
                break;
            }
            intentos++;
        }
    }

    public void registrarVisita(int x, int y) {
        if (estaEnLimites(x, y)) {
            visitado[y][x] = true;
            ruta.add(new Point(x, y));
        }
    }

    public boolean comer(int x, int y) {
        if (estaEnLimites(x, y) && mapa[y][x] == COMIDA) {
            mapa[y][x] = SUELO;
            spawnComida(); // La comida reaparece en otro lado
            return true;
        }
        return false;
    }

    public boolean estaEnLimites(int x, int y) {
        return x >= 0 && x < ANCHO && y >= 0 && y < ALTO;
    }

    public int getTipoCasilla(int x, int y) {
        if (!estaEnLimites(x, y)) return MURO;
        return mapa[y][x];
    }

    public List<Point> getRuta() {
        return this.ruta;
    }
}
