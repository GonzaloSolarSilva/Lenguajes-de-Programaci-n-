package com.saborcriollo.pedidos.dto;

import java.math.BigDecimal;

import com.saborcriollo.pedidos.model.enums.Estacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductoRequest(@NotBlank @Size(max = 100) String nombre,
                              @Size(max = 255) String descripcion,
                              @NotNull @PositiveOrZero BigDecimal precio,
                              @NotNull Estacion estacion,
                              @NotNull Long idCategoria) {
}
