package com.saborcriollo.pedidos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saborcriollo.pedidos.dto.VentaResumenDto;
import com.saborcriollo.pedidos.exception.ReglaNegocioException;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;
import com.saborcriollo.pedidos.repository.DetallePedidoRepository;

/** Reportes de ventas para el administrador (objetivo específico 5). */
@Service
public class ReporteService {

    private final DetallePedidoRepository detalleRepository;

    public ReporteService(DetallePedidoRepository detalleRepository) {
        this.detalleRepository = detalleRepository;
    }

    /** Ventas de pedidos PAGADOS entre dos fechas (ambas inclusive), por turno y producto. */
    @Transactional(readOnly = true)
    public List<VentaResumenDto> generarReporteVentas(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha inicial no puede ser posterior a la final");
        }
        return detalleRepository.ventasPorTurnoYProducto(EstadoPedido.PAGADO,
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }
}
