package com.saborcriollo.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborcriollo.pedidos.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
