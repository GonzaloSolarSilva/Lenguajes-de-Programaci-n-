package com.saborcriollo.pedidos.dto;

import com.saborcriollo.pedidos.model.enums.MetodoPago;

import jakarta.validation.constraints.NotNull;

public record PagoRequest(@NotNull Long idCajero, @NotNull MetodoPago metodoPago) {
}
