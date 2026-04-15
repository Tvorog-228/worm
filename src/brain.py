import numpy as np
import torch
import torch.nn as nn


class Brain300(nn.Module):
    def __init__(self):
        super(Brain300, self).__init__()
        self.num_neurons = 300
        self.num_input = 14  # Definidos en body.py
        self.num_output = 5  # [Quieto, N, S, E, O]

        # 1. Pesos de la red (300x300)
        # Usamos una sola matriz para permitir recurrencia (interneuronas hablándose)
        self.weights = nn.Parameter(torch.randn(300, 300) * 0.1)

        # 2. Máscara Biológica (Estructura de conexiones)
        self.register_buffer("mask", self._crear_mascara())

        # 3. Estado interno (Voltaje de las neuronas)
        self.state = torch.zeros(300)

        # Parámetro de "olvido" o decaimiento (Leakage)
        self.tau = 0.8

    def _crear_mascara(self):
        """Define quién puede conectarse con quién."""
        m = torch.zeros((300, 300))

        # Bloques de neuronas:
        # 0-49: Sensoriales | 50-249: Interneuronas | 250-299: Motoras

        # Sensoriales -> Interneuronas
        m[0:50, 50:250] = 1

        # Interneuronas -> Interneuronas (Recurrencia esparcida al 15%)
        rec_mask = torch.rand((200, 200)) < 0.15
        m[50:250, 50:250] = rec_mask.float()

        # Interneuronas -> Motoras
        m[50:250, 250:300] = 1

        return m

    def forward(self, x_input):
        """
        x_input: array de 14 sensores
        """
        with torch.no_grad():
            # Convertir input a tensor y mapearlo a las neuronas sensoriales (0-13)
            inputs = torch.zeros(300)
            inputs[0 : self.num_input] = torch.tensor(x_input, dtype=torch.float32)

            # Aplicar la máscara a los pesos (solo existen las conexiones de la máscara)
            w_efectivos = self.weights * self.mask

            # Ecuación de dinámica neuronal:
            # Nuevo Estado = (Viejo Estado * Decaimiento) + Activación de conexiones
            activacion = torch.matmul(w_efectivos.t(), self.state) + inputs
            self.state = (self.tau * self.state) + (1 - self.tau) * torch.tanh(
                activacion
            )

            # Extraer las 5 neuronas motoras (250 a 254)
            motor_v = self.state[250 : 250 + self.num_output]

            # Devolver el índice de la neurona con más "voltaje"
            return torch.argmax(motor_v).item()

    def reset_state(self):
        """Limpia la memoria del cerebro (ej. al morir o dormir)"""
        self.state = torch.zeros(300)
