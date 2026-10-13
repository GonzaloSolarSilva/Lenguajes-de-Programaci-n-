package com.saborcriollo.pedidos.dto;

import com.saborcriollo.pedidos.model.enums.EstadoPedido;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(@NotNull EstadoPedido estado) {
}
