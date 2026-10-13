package com.saborcriollo.pedidos.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/** Cantidad de un insumo que consume una unidad de un producto (resuelve la relación N:M). */
@Entity
@Table(name = "receta_item")
public class RecetaItem {

    @EmbeddedId
    private RecetaItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idInsumo")
    @JoinColumn(name = "id_insumo")
    private Insumo insumo;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    protected RecetaItem() {
    }

    public RecetaItem(Producto producto, Insumo insumo, BigDecimal cantidad) {
        this.id = new RecetaItemId(producto.getIdProducto(), insumo.getIdInsumo());
        this.producto = producto;
        this.insumo = insumo;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }
}
