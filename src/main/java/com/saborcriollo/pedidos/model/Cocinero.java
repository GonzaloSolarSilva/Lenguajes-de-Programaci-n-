package com.saborcriollo.pedidos.model;

import com.saborcriollo.pedidos.exception.ReglaNegocioException;
import com.saborcriollo.pedidos.model.enums.Estacion;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/** Usuario de cocina que prepara los pedidos de su estación. */
@Entity
@DiscriminatorValue("COCINERO")
public class Cocinero extends Usuario {

    @Enumerated(EnumType.STRING)
    @Column(name = "estacion", length = 20)
    private Estacion estacion;

    protected Cocinero() {
    }

    public Cocinero(String nombres, String apellidos, String username, String clave, Estacion estacion) {
        super(nombres, apellidos, username, clave);
        this.estacion = estacion;
    }

    /** Avanza el pedido un paso en el flujo de cocina: RECIBIDO -> EN_PREPARACION -> LISTO. */
    public void actualizarEstado(Pedido pedido) {
        switch (pedido.getEstado()) {
            case RECIBIDO -> pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
            case EN_PREPARACION -> pedido.cambiarEstado(EstadoPedido.LISTO);
            default -> throw new ReglaNegocioException(
                    "Cocina no puede avanzar un pedido en estado " + pedido.getEstado());
        }
    }

    @Override
    public String getRol() {
        return "COCINERO";
    }

    public Estacion getEstacion() {
        return estacion;
    }
}
