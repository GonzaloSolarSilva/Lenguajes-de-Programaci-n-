package com.saborcriollo.pedidos.dto;

import java.math.BigDecimal;

import com.saborcriollo.pedidos.model.Insumo;

public record InsumoResponse(Long idInsumo, String nombre, String unidadMedida,
                             BigDecimal stockActual, BigDecimal stockMinimo, boolean bajoMinimo) {

    public static InsumoResponse from(Insumo i) {
        return new InsumoResponse(i.getIdInsumo(), i.getNombre(), i.getUnidadMedida(),
                i.getStockActual(), i.getStockMinimo(), i.estaBajoMinimo());
    }
}
