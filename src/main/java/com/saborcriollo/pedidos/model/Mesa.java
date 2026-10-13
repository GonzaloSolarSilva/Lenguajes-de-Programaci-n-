package com.saborcriollo.pedidos.model;

import com.saborcriollo.pedidos.exception.ReglaNegocioException;
import com.saborcriollo.pedidos.model.enums.EstadoMesa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Mesa física del salón. */
@Entity
@Table(name = "mesa")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMesa;

    @Column(nullable = false, unique = true)
    private int numero;

    @Column(nullable = false)
    private int capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoMesa estado = EstadoMesa.LIBRE;

    protected Mesa() {
    }

    public Mesa(int numero, int capacidad) {
        this.numero = numero;
        this.capacidad = capacidad;
    }

    /** Marca la mesa como ocupada; falla si ya lo estaba. */
    public void ocupar() {
        if (estado == EstadoMesa.OCUPADA) {
            throw new ReglaNegocioException("La mesa " + numero + " ya está ocupada");
        }
        estado = EstadoMesa.OCUPADA;
    }

    /** Marca la mesa como libre. */
    public void liberar() {
        estado = EstadoMesa.LIBRE;
    }

    public Long getIdMesa() {
        return idMesa;
    }

    public int getNumero() {
        return numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public EstadoMesa getEstado() {
        return estado;
    }
}
