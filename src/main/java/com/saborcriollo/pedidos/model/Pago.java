package com.saborcriollo.pedidos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.saborcriollo.pedidos.model.enums.MetodoPago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** Cobro registrado de un pedido (un pedido tiene como máximo un pago). */
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", unique = true)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cajero")
    private Cajero cajero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private MetodoPago metodoPago;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    protected Pago() {
    }

    public Pago(Pedido pedido, Cajero cajero, MetodoPago metodoPago, BigDecimal monto) {
        this.pedido = pedido;
        this.cajero = cajero;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.fechaHora = LocalDateTime.now();
    }

    public Long getIdPago() {
        return idPago;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Cajero getCajero() {
        return cajero;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
