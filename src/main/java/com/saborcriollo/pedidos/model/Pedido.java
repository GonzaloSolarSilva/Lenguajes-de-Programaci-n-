package com.saborcriollo.pedidos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.saborcriollo.pedidos.exception.ReglaNegocioException;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;
import com.saborcriollo.pedidos.model.enums.Turno;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Orden de una mesa. Encapsula sus reglas: solo se modifica mientras está RECIBIDO,
 * el total siempre se recalcula a partir de sus detalles y los cambios de estado
 * siguen las transiciones permitidas por EstadoPedido.
 */
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mesero")
    private Mesero mesero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Turno turno;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(Mesa mesa, Mesero mesero, Turno turno) {
        this.mesa = mesa;
        this.mesero = mesero;
        this.turno = turno;
        this.fechaHora = LocalDateTime.now();
        this.estado = EstadoPedido.RECIBIDO;
    }

    /** Falla si el pedido ya no admite cambios en su contenido. */
    public void verificarModificable() {
        if (estado != EstadoPedido.RECIBIDO) {
            throw new ReglaNegocioException(
                    "Solo se pueden agregar productos a un pedido en estado RECIBIDO (estado actual: " + estado + ")");
        }
    }

    /** Agrega una línea al pedido y recalcula el total. */
    public void agregarDetalle(DetallePedido detalle) {
        verificarModificable();
        detalles.add(detalle);
        detalle.setPedido(this);
        calcularTotal();
    }

    /** Suma los subtotales de todos los detalles. */
    public BigDecimal calcularTotal() {
        total = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total;
    }

    /** Cambia el estado respetando las transiciones permitidas. */
    public void cambiarEstado(EstadoPedido nuevo) {
        if (!estado.puedePasarA(nuevo)) {
            throw new ReglaNegocioException(
                    "No se puede pasar el pedido de " + estado + " a " + nuevo);
        }
        if (nuevo == EstadoPedido.EN_PREPARACION && detalles.isEmpty()) {
            throw new ReglaNegocioException("No se puede enviar a cocina un pedido sin productos");
        }
        estado = nuevo;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public Mesero getMesero() {
        return mesero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public Turno getTurno() {
        return turno;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }
}
