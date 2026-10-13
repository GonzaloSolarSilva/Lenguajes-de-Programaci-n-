package com.saborcriollo.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborcriollo.pedidos.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
