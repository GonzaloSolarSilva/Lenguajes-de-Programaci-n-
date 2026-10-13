package com.saborcriollo.pedidos.dto;

import jakarta.validation.constraints.NotNull;

public record AvanceRequest(@NotNull Long idCocinero) {
}
