package com.saborcriollo.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborcriollo.pedidos.model.Cajero;

public interface CajeroRepository extends JpaRepository<Cajero, Long> {
}
