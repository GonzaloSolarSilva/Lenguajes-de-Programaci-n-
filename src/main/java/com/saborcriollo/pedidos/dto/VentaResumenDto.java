package com.saborcriollo.pedidos.dto;

import java.math.BigDecimal;

import com.saborcriollo.pedidos.model.enums.Turno;

/** Fila del reporte de ventas por turno y producto. */
public record VentaResumenDto(Turno turno, String producto, Long unidadesVendidas, BigDecimal montoVendido) {
}
