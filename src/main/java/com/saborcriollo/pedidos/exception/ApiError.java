package com.saborcriollo.pedidos.exception;

import java.time.LocalDateTime;

/** Cuerpo estándar de las respuestas de error de la API. */
public record ApiError(int estado, String mensaje, LocalDateTime fecha) {
}
