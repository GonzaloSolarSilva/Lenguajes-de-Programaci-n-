package com.saborcriollo.pedidos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saborcriollo.pedidos.dto.VentaResumenDto;
import com.saborcriollo.pedidos.model.DetallePedido;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    /** Ventas de pedidos pagados agrupadas por turno y producto en un rango de fechas. */
    @Query("select new com.saborcriollo.pedidos.dto.VentaResumenDto(p.turno, pr.nombre, sum(d.cantidad), sum(d.subtotal)) "
            + "from DetallePedido d join d.pedido p join d.producto pr "
            + "where p.estado = :estado and p.fechaHora >= :desde and p.fechaHora < :hasta "
            + "group by p.turno, pr.nombre "
            + "order by p.turno, sum(d.subtotal) desc")
    List<VentaResumenDto> ventasPorTurnoYProducto(@Param("estado") EstadoPedido estado,
                                                  @Param("desde") LocalDateTime desde,
                                                  @Param("hasta") LocalDateTime hasta);
}
