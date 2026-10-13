package com.saborcriollo.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborcriollo.pedidos.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
