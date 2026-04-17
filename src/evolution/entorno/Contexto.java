package evolution.entorno;

public class Contexto {

    private Entorno entorno;
    private Agente agente;
    private Estadisticas estadisticas;

    // Tipos de acción que el gusano puede devolver
    public enum Accion {
        QUIETO,
        NORTE,
        SUR,
        ESTE,
        OESTE,
    }

    public Contexto(long semilla) {
        this.entorno = new Entorno(semilla);
        // Empezamos en una posición segura aleatoria
        int startX, startY;
        do {
            startX = (int) (Math.random() * (Entorno.ANCHO - 2)) + 1;
            startY = (int) (Math.random() * (Entorno.ALTO - 2)) + 1;
        } while (entorno.getTipoCasilla(startX, startY) == Entorno.MURO);

        this.agente = new Agente(startX, startY);
        this.estadisticas = new Estadisticas();
        this.entorno.registrarVisita(startX, startY);
    }

    public void ejecutarAccion(int codigoAccion) {
        if (!agente.vivo || agente.accionTomada) return;

        Accion accion = Accion.values()[codigoAccion];

        // Lógica de Dormir
        if (agente.dormido) {
            if (accion != Accion.QUIETO) {
                agente.dormido = false; // Despierta si intenta moverse
            } else {
                agente.accionTomada = true;
                return; // Sigue durmiendo
            }
        }

        if (accion == Accion.QUIETO && agente.sueno > 50.0) {
            agente.dormido = true;
            agente.accionTomada = true;
            return;
        }

        // Lógica de Movimiento
        int dx = 0,
            dy = 0;
        if (accion == Accion.NORTE) dy = -1;
        else if (accion == Accion.SUR) dy = 1;
        else if (accion == Accion.ESTE) dx = 1;
        else if (accion == Accion.OESTE) dx = -1;

        if (dx != 0 || dy != 0) {
            int nx = agente.x + dx;
            int ny = agente.y + dy;

            if (entorno.getTipoCasilla(nx, ny) != Entorno.MURO) {
                agente.x = nx;
                agente.y = ny;
                entorno.registrarVisita(nx, ny);

                if (entorno.comer(nx, ny)) {
                    agente.energia = Math.min(100.0, agente.energia + 30.0);
                    estadisticas.comidaIngerida++;
                }
            } else {
                agente.energia -= 2.0; // Chocar duele
                estadisticas.colisiones++;
            }
        }

        agente.accionTomada = true;
    }

    public void actualizarMetabolismo() {
        if (!agente.vivo) return;

        if (agente.dormido) {
            double factorRecuperacion = (entorno.getTipoCasilla(
                    agente.x,
                    agente.y
                ) ==
                Entorno.DESCANSO)
                ? 2.0
                : 1.0;
            agente.sueno = Math.max(
                0.0,
                agente.sueno - (5.0 * factorRecuperacion)
            );
            agente.energia -= 0.1; // Gasta poca energía al dormir
            estadisticas.tiempoDormido++;
        } else {
            agente.energia -= 0.5; // Gasta energía por estar despierto
            agente.sueno = Math.min(100.0, agente.sueno + 0.2); // Se cansa
        }

        if (agente.energia <= 0.0) {
            agente.vivo = false;
        }

        agente.accionTomada = false; // Reset para el siguiente tick
        estadisticas.ticksSobrevividos++;
    }

    // Este método AHORA recibe la distancia generada por el genoma
    public double leerSensor(
        evolution.arboles.NodoCondicional.AtributoSensor sensor,
        int distancia
    ) {
        switch (sensor) {
            case OBSTACULO_N:
                return percibirRaycast(0, -1, Entorno.MURO, distancia);
            case OBSTACULO_S:
                return percibirRaycast(0, 1, Entorno.MURO, distancia);
            case OBSTACULO_E:
                return percibirRaycast(1, 0, Entorno.MURO, distancia);
            case OBSTACULO_O:
                return percibirRaycast(-1, 0, Entorno.MURO, distancia);
            case COMIDA_N:
                return percibirRaycast(0, -1, Entorno.COMIDA, distancia);
            case COMIDA_S:
                return percibirRaycast(0, 1, Entorno.COMIDA, distancia);
            case COMIDA_E:
                return percibirRaycast(1, 0, Entorno.COMIDA, distancia);
            case COMIDA_O:
                return percibirRaycast(-1, 0, Entorno.COMIDA, distancia);
            case DESCANSO_N:
                return percibirRaycast(0, -1, Entorno.DESCANSO, distancia);
            case DESCANSO_S:
                return percibirRaycast(0, 1, Entorno.DESCANSO, distancia);
            case DESCANSO_E:
                return percibirRaycast(1, 0, Entorno.DESCANSO, distancia);
            case DESCANSO_O:
                return percibirRaycast(-1, 0, Entorno.DESCANSO, distancia);
            case ENERGIA:
                return agente.energia;
            case SUENO:
                return agente.sueno;
            default:
                return 0.0;
        }
    }

    /**
     * "Rayo de Visión": Comprueba si hay un objetivo específico exactamente a 'X' distancia.
     * Devuelve 1.0 si el objetivo está ahí, 0.0 si no lo está o si un muro bloquea la vista antes.
     */
    private double percibirRaycast(
        int dx,
        int dy,
        int tipoObjetivo,
        int distanciaFocal
    ) {
        for (int i = 1; i <= distanciaFocal; i++) {
            int nx = agente.x + (dx * i);
            int ny = agente.y + (dy * i);

            int celdaViendo = entorno.getTipoCasilla(nx, ny);

            // Si chocamos con un muro antes de llegar a la distancia focal, perdemos visión
            if (celdaViendo == Entorno.MURO && i < distanciaFocal) {
                return 0.0; // La vista está bloqueada
            }

            // Si llegamos a la distancia exacta que estamos mirando
            if (i == distanciaFocal) {
                return (celdaViendo == tipoObjetivo) ? 1.0 : 0.0;
            }
        }
        return 0.0;
    }

    // --- SENSORES (Devuelven 1.0 si detectan algo adyacente, 0.0 si no) ---

    public double percibirObstaculo(int dx, int dy) {
        return entorno.getTipoCasilla(agente.x + dx, agente.y + dy) ==
            Entorno.MURO
            ? 1.0
            : 0.0;
    }

    public double percibirComida(int dx, int dy) {
        return entorno.getTipoCasilla(agente.x + dx, agente.y + dy) ==
            Entorno.COMIDA
            ? 1.0
            : 0.0;
    }

    public double percibirDescanso(int dx, int dy) {
        return entorno.getTipoCasilla(agente.x + dx, agente.y + dy) ==
            Entorno.DESCANSO
            ? 1.0
            : 0.0;
    }

    public double getNivelEnergia() {
        return agente.energia / 100.0;
    }

    public double getNivelSueno() {
        return agente.sueno / 100.0;
    }

    public Agente getAgente() {
        return agente;
    }

    public Entorno getEntorno() {
        return entorno;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }
}
