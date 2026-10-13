package com.saborcriollo.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborcriollo.pedidos.model.Mesero;

public interface MeseroRepository extends JpaRepository<Mesero, Long> {
}
