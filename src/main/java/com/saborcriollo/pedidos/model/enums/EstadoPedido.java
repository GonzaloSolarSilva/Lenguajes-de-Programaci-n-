package com.saborcriollo.pedidos.model.enums;

/** Ciclo de vida de un pedido y sus transiciones permitidas. */
public enum EstadoPedido {
    RECIBIDO, EN_PREPARACION, LISTO, SERVIDO, PAGADO, CANCELADO;

    /** Indica si desde el estado actual se puede pasar al estado destino. */
    public boolean puedePasarA(EstadoPedido destino) {
        return switch (this) {
            case RECIBIDO -> destino == EN_PREPARACION || destino == CANCELADO;
            case EN_PREPARACION -> destino == LISTO || destino == CANCELADO;
            case LISTO -> destino == SERVIDO;
            case SERVIDO -> destino == PAGADO;
            case PAGADO, CANCELADO -> false;
        };
    }
}
