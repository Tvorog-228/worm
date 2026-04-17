package evolution.entorno;

public class Agente {

    public int x;
    public int y;

    // Energía de 0.0 (muerto) a 100.0 (lleno)
    public double energia = 50.0;
    // Sueño de 0.0 (despierto) a 100.0 (exhausto)
    public double sueno = 0.0;

    public boolean vivo = true;
    public boolean dormido = false;

    public int girosConsecutivos = 0;
    public boolean accionTomada = false;

    public Agente(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }
}
