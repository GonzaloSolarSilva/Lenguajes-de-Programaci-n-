package com.saborcriollo.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saborcriollo.pedidos.model.RecetaItem;
import com.saborcriollo.pedidos.model.RecetaItemId;

public interface RecetaItemRepository extends JpaRepository<RecetaItem, RecetaItemId> {

    /** Receta de un producto, trayendo los insumos en la misma consulta. */
    @Query("select r from RecetaItem r join fetch r.insumo where r.producto.idProducto = :idProducto")
    List<RecetaItem> buscarPorProducto(@Param("idProducto") Long idProducto);
}
