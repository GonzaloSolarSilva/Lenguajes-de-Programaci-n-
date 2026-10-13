package com.saborcriollo.pedidos.dto;

import java.math.BigDecimal;

import com.saborcriollo.pedidos.model.Producto;
import com.saborcriollo.pedidos.model.enums.Estacion;

public record ProductoResponse(Long idProducto, String nombre, String descripcion, BigDecimal precio,
                               Estacion estacion, String categoria, boolean disponible) {

    /** Debe invocarse dentro de una transacción (la categoría es LAZY). */
    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(p.getIdProducto(), p.getNombre(), p.getDescripcion(), p.getPrecio(),
                p.getEstacion(), p.getCategoria().getNombre(), p.isDisponible());
    }
}
