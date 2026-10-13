package com.saborcriollo.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborcriollo.pedidos.model.Insumo;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    List<Insumo> findAllByOrderByNombreAsc();
}
