package com.saborcriollo.pedidos.model;

import java.math.BigDecimal;

import com.saborcriollo.pedidos.exception.ReglaNegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Ingrediente del restaurante con control de stock. */
@Entity
@Table(name = "insumo")
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInsumo;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String unidadMedida;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal stockActual = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    protected Insumo() {
    }

    public Insumo(String nombre, String unidadMedida, BigDecimal stockActual, BigDecimal stockMinimo) {
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }

    /** Indica si hay existencias suficientes para la cantidad pedida. */
    public boolean hayStock(BigDecimal cantidad) {
        return stockActual.compareTo(cantidad) >= 0;
    }

    /** Descuenta stock; lanza error si no alcanza. */
    public void descontarStock(BigDecimal cantidad) {
        if (!hayStock(cantidad)) {
            throw new ReglaNegocioException("Stock insuficiente de " + nombre
                    + " (disponible: " + stockActual + " " + unidadMedida + ")");
        }
        stockActual = stockActual.subtract(cantidad);
    }

    /** Suma stock por una reposición. */
    public void reponerStock(BigDecimal cantidad) {
        if (cantidad.signum() <= 0) {
            throw new ReglaNegocioException("La cantidad a reponer debe ser mayor que cero");
        }
        stockActual = stockActual.add(cantidad);
    }

    /** Indica si el stock actual está en o por debajo del mínimo. */
    public boolean estaBajoMinimo() {
        return stockActual.compareTo(stockMinimo) <= 0;
    }

    public Long getIdInsumo() {
        return idInsumo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }
}
