import os
import time


def dibujar_consola(mundo, gusano):
    # Diccionario de representación
    simbolos = {
        0: " ",  # VACIO (un espacio o un punto suave)
        1: "█",  # OBSTACULO (bloque sólido)
        2: "ø",  # COMIDA
        3: "▒",  # DESCANSO (sombreado)
        4: "X",  # PELIGRO
    }

    # Limpiar la terminal (funciona en Windows 'cls' y Unix 'clear')
    os.system("cls" if os.name == "nt" else "clear")

    print("=" * (mundo.ancho + 2))
    for y in range(mundo.alto):
        linea = "|"
        for x in range(mundo.ancho):
            # Si el gusano está en esta posición, lo dibujamos a él
            if x == gusano.x and y == gusano.y:
                # Si está durmiendo, ponemos una 'z', si no, una 'S'
                char = "z" if gusano.estado_dormido else "S"
            else:
                tipo = mundo.obtener_celda(x, y)
                char = simbolos.get(tipo, "?")

            linea += char
        linea += "|"
        print(linea)
    print("=" * (mundo.ancho + 2))

    # Stats debajo del mapa
    estado = "DURMIENDO" if gusano.estado_dormido else "ACTIVO"
    print(
        f" ENERGÍA: {'█' * int(gusano.energia * 10)}{'░' * (10 - int(gusano.energia * 10))} | "
        f"SUEÑO: {gusano.sueno:.2f} | ESTADO: {estado}"
    )
