package com.saborcriollo.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.saborcriollo.pedidos.model.Pedido;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;
import com.saborcriollo.pedidos.model.enums.Turno;

public record PedidoResponse(Long idPedido, int numeroMesa, String mesero, String cliente,
                             LocalDateTime fechaHora, EstadoPedido estado, Turno turno,
                             BigDecimal total, List<DetalleResponse> detalles) {

    /** Debe invocarse dentro de una transacción (las asociaciones son LAZY). */
    public static PedidoResponse from(Pedido p) {
        String cliente = p.getCliente() == null ? null : p.getCliente().getNombres();
        List<DetalleResponse> detalles = p.getDetalles().stream().map(DetalleResponse::from).toList();
        return new PedidoResponse(p.getIdPedido(), p.getMesa().getNumero(), p.getMesero().getNombreCompleto(),
                cliente, p.getFechaHora(), p.getEstado(), p.getTurno(), p.getTotal(), detalles);
    }
}
