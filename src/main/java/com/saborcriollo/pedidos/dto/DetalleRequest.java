package com.saborcriollo.pedidos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DetalleRequest(@NotNull Long idProducto,
                             @NotNull @Positive Integer cantidad,
                             @Size(max = 200) String observacion) {
}
