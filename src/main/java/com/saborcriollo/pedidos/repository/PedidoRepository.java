package com.saborcriollo.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborcriollo.pedidos.model.Pedido;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByFechaHoraDesc();

    List<Pedido> findByEstadoOrderByFechaHoraAsc(EstadoPedido estado);
}
