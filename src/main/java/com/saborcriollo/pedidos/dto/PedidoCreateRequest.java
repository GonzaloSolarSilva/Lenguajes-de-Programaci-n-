package com.saborcriollo.pedidos.dto;

import jakarta.validation.constraints.NotNull;

public record PedidoCreateRequest(@NotNull Long idMesa, @NotNull Long idMesero, Long idCliente) {
}
