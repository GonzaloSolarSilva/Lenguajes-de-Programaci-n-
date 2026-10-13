package com.saborcriollo.pedidos.dto;

import java.math.BigDecimal;

import com.saborcriollo.pedidos.model.DetallePedido;

public record DetalleResponse(Long idDetalle, String producto, int cantidad,
                              BigDecimal precioUnitario, BigDecimal subtotal, String observacion) {

    public static DetalleResponse from(DetallePedido d) {
        return new DetalleResponse(d.getIdDetalle(), d.getProducto().getNombre(), d.getCantidad(),
                d.getPrecioUnitario(), d.getSubtotal(), d.getObservacion());
    }
}
