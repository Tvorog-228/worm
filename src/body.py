import numpy as np


class GusanoCuerpo:
    def __init__(self, mundo, x=5, y=5):
        self.mundo = mundo
        self.x = x
        self.y = y

        # Homeostasis (0.0 a 1.0)
        self.energia = 1.0
        self.sueno = 0.0  # 0 es descansado, 1 es agotado
        self.vivo = True
        self.estado_dormido = False

    def obtener_sensores(self):
        """
        Genera el vector de entrada para las primeras neuronas del cerebro.
        Mapea el entorno inmediato y el estado interno.
        """
        # Vamos a definir 14 sensores básicos por ahora
        sensores = np.zeros(14)

        # Direcciones: [Norte, Sur, Este, Oeste]
        direcciones = [(0, 1), (0, -1), (1, 0), (-1, 0)]

        for i, (dx, dy) in enumerate(direcciones):
            tipo = self.mundo.obtener_celda(self.x + dx, self.y + dy)
            # Sensores de Obstáculo (4 neuronas)
            if tipo == self.mundo.OBSTACULO:
                sensores[i] = 1.0
            # Sensores de Comida (4 neuronas)
            if tipo == self.mundo.COMIDA:
                sensores[i + 4] = 1.0
            # Sensores de Zona Descanso (4 neuronas)
            if tipo == self.mundo.DESCANSO:
                sensores[i + 8] = 1.0

        # Sensores Internos (2 neuronas)
        sensores[12] = self.energia
        sensores[13] = self.sueno

        return sensores

    def actuar(self, decision_cerebro):
        """
        Recibe la decisión del motor (0-4) y ejecuta la física.
        0: Quieto/Dormir, 1: Norte, 2: Sur, 3: Este, 4: Oeste
        """
        if self.estado_dormido:
            # Si decide moverse (1-4), se despierta
            if decision_cerebro > 0:
                self.estado_dormido = False
            else:
                return  # Sigue durmiendo

        # Si decide 0 y está cansado, duerme
        if decision_cerebro == 0 and self.sueno > 0.5:
            self.estado_dormido = True
            return

        # Movimiento
        dx, dy = 0, 0
        if decision_cerebro == 1:
            dy = 1
        elif decision_cerebro == 2:
            dy = -1
        elif decision_cerebro == 3:
            dx = 1
        elif decision_cerebro == 4:
            dx = -1

        nx, ny = self.x + dx, self.y + dy

        # Lógica física
        celda = self.mundo.obtener_celda(nx, ny)
        if celda != self.mundo.OBSTACULO:
            self.x, self.y = nx, ny
            if self.mundo.comer(self.x, self.y):
                self.energia = min(1.0, self.energia + 0.3)
        else:
            self.energia -= 0.02  # Penalización por chocar

    def actualizar_metabolismo(self):
        """Actualiza hambre, sueño y vida cada ciclo."""
        if self.estado_dormido:
            factor = (
                2.0
                if self.mundo.obtener_celda(self.x, self.y) == self.mundo.DESCANSO
                else 1.0
            )
            self.sueno = max(0.0, self.sueno - 0.05 * factor)
            self.energia -= 0.001
        else:
            self.energia -= 0.005  # Gasto por estar despierto
            self.sueno = min(1.0, self.sueno + 0.002)

        if self.energia <= 0:
            self.vivo = False
