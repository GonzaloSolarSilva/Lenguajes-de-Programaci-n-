package com.saborcriollo.pedidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Comensal registrado (es opcional dentro de un pedido). */
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(unique = true, length = 15)
    private String documento;

    @Column(length = 15)
    private String telefono;

    protected Cliente() {
    }

    public Cliente(String nombres, String documento, String telefono) {
        this.nombres = nombres;
        this.documento = documento;
        this.telefono = telefono;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public String getNombres() {
        return nombres;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }
}
