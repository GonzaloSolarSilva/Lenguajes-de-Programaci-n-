package com.saborcriollo.pedidos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Usuario que gestiona la carta, el inventario y consulta los reportes.
 * Los reportes se generan en ReporteService (capa de servicios).
 */
@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    protected Administrador() {
    }

    public Administrador(String nombres, String apellidos, String username, String clave) {
        super(nombres, apellidos, username, clave);
    }

    @Override
    public String getRol() {
        return "ADMINISTRADOR";
    }
}
