package com.saborcriollo.pedidos.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReposicionRequest(@NotNull @Positive BigDecimal cantidad) {
}
