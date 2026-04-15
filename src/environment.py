import numpy as np

class MundoGusano:
    # Identificadores de tipos de celda
    VACIO = 0
    OBSTACULO = 1
    COMIDA = 2
    DESCANSO = 3
    PELIGRO = 4

    def __init__(self, ancho=40, alto=40):
        self.ancho = ancho
        self.alto = alto
        self.mapa = np.zeros((ancho, alto), dtype=int)
        self._configurar_escenario()

    def _configurar_escenario(self):
        # 1. Bordes (Paredes)
        self.mapa[0,:] = self.OBSTACULO
        self.mapa[-1,:] = self.OBSTACULO
        self.mapa[:,0] = self.OBSTACULO
        self.mapa[:,-1] = self.OBSTACULO

        # 2. Obstáculo central (un bloque)
        self.mapa[15:25, 18:22] = self.OBSTACULO

        # 3. Zona de Descanso (esquina inferior derecha)
        self.mapa[30:38, 30:38] = self.DESCANSO

        # 4. Comida inicial (puntos dispersos)
        for _ in range(15):
            self.spawn_comida()

    def spawn_comida(self):
        """Coloca una unidad de comida en un lugar vacío aleatorio."""
        intentos = 0
        while intentos < 100:
            rx = np.random.randint(1, self.ancho-1)
            ry = np.random.randint(1, self.alto-1)
            if self.mapa[rx, ry] == self.VACIO:
                self.mapa[rx, ry] = self.COMIDA
                break
            intentos += 1

    def obtener_celda(self, x, y):
        if 0 <= x < self.ancho and 0 <= y < self.alto:
            return self.mapa[x, y]
        return self.OBSTACULO

    def comer(self, x, y):
        if self.mapa[x, y] == self.COMIDA:
            self.mapa[x, y] = self.VACIO
            self.spawn_comida() # La comida se regenera en otro lado
            return True
        return False
