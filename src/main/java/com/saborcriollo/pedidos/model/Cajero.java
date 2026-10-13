package com.saborcriollo.pedidos.model;

import com.saborcriollo.pedidos.exception.ReglaNegocioException;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;
import com.saborcriollo.pedidos.model.enums.MetodoPago;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** Usuario que cobra y cierra los pedidos. */
@Entity
@DiscriminatorValue("CAJERO")
public class Cajero extends Usuario {

    protected Cajero() {
    }

    public Cajero(String nombres, String apellidos, String username, String clave) {
        super(nombres, apellidos, username, clave);
    }

    /**
     * Cobra un pedido ya servido: genera el pago, marca el pedido como PAGADO
     * y libera la mesa.
     */
    public Pago cobrarPedido(Pedido pedido, MetodoPago metodo) {
        if (pedido.getEstado() != EstadoPedido.SERVIDO) {
            throw new ReglaNegocioException(
                    "Solo se puede cobrar un pedido SERVIDO (estado actual: " + pedido.getEstado() + ")");
        }
        Pago pago = new Pago(pedido, this, metodo, pedido.getTotal());
        pedido.cambiarEstado(EstadoPedido.PAGADO);
        pedido.getMesa().liberar();
        return pago;
    }

    @Override
    public String getRol() {
        return "CAJERO";
    }
}
