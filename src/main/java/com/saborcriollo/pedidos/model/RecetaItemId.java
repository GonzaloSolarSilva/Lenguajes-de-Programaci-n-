package com.saborcriollo.pedidos.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Clave primaria compuesta de RecetaItem (producto + insumo). */
@Embeddable
public class RecetaItemId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "id_insumo")
    private Long idInsumo;

    protected RecetaItemId() {
    }

    public RecetaItemId(Long idProducto, Long idInsumo) {
        this.idProducto = idProducto;
        this.idInsumo = idInsumo;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public Long getIdInsumo() {
        return idInsumo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecetaItemId)) {
            return false;
        }
        RecetaItemId otro = (RecetaItemId) o;
        return Objects.equals(idProducto, otro.idProducto) && Objects.equals(idInsumo, otro.idInsumo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProducto, idInsumo);
    }
}
