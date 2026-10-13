package com.saborcriollo.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborcriollo.pedidos.model.Cocinero;

public interface CocineroRepository extends JpaRepository<Cocinero, Long> {
}
