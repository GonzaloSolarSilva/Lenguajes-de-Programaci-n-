package com.saborcriollo.pedidos.model;

import java.time.LocalTime;

import com.saborcriollo.pedidos.model.enums.Turno;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/** Usuario que atiende las mesas y registra los pedidos. */
@Entity
@DiscriminatorValue("MESERO")
public class Mesero extends Usuario {

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", length = 10)
    private Turno turno;

    protected Mesero() {
    }

    public Mesero(String nombres, String apellidos, String username, String clave, Turno turno) {
        super(nombres, apellidos, username, clave);
        this.turno = turno;
    }

    /** Abre un pedido nuevo para la mesa indicada y la marca como ocupada. */
    public Pedido registrarPedido(Mesa mesa) {
        mesa.ocupar();
        return new Pedido(mesa, this, Turno.deHora(LocalTime.now()));
    }

    @Override
    public String getRol() {
        return "MESERO";
    }

    public Turno getTurno() {
        return turno;
    }
}
