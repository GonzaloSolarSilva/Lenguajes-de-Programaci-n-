package com.saborcriollo.pedidos.dto;

import com.saborcriollo.pedidos.model.Mesa;
import com.saborcriollo.pedidos.model.enums.EstadoMesa;

public record MesaResponse(Long idMesa, int numero, int capacidad, EstadoMesa estado) {

    public static MesaResponse from(Mesa m) {
        return new MesaResponse(m.getIdMesa(), m.getNumero(), m.getCapacidad(), m.getEstado());
    }
}
