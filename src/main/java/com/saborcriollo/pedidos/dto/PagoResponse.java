package com.saborcriollo.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.saborcriollo.pedidos.model.Pago;
import com.saborcriollo.pedidos.model.enums.MetodoPago;

public record PagoResponse(Long idPago, Long idPedido, MetodoPago metodoPago,
                           BigDecimal monto, LocalDateTime fechaHora) {

    public static PagoResponse from(Pago p) {
        return new PagoResponse(p.getIdPago(), p.getPedido().getIdPedido(), p.getMetodoPago(),
                p.getMonto(), p.getFechaHora());
    }
}
