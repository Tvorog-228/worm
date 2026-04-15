# main.py modificado
import time

from src.body import GusanoCuerpo
from src.brain import Brain300
from src.environment import MundoGusano
from src.utils import dibujar_consola


def run_simulacion():
    mundo = MundoGusano(ancho=40, alto=20)
    gusano = GusanoCuerpo(mundo)
    cerebro = Brain300()  # ¡Instanciamos el cerebro de 300 neuronas!

    try:
        while gusano.vivo:
            # 1. El cuerpo siente el entorno
            sensores = gusano.obtener_sensores()

            # 2. El cerebro procesa y decide
            accion = cerebro.forward(sensores)

            # 3. El cuerpo ejecuta y el metabolismo se actualiza
            gusano.actuar(accion)
            gusano.actualizar_metabolismo()

            # 4. Ver el resultado
            dibujar_consola(mundo, gusano)

            time.sleep(0.05)

    except KeyboardInterrupt:
        print("\nSimulación terminada.")


if __name__ == "__main__":
    run_simulacion()
