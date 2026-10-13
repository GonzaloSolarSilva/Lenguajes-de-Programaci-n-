package com.saborcriollo.pedidos.dto;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadRequest(@NotNull Boolean disponible) {
}
