package com.saborcriollo.pedidos.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saborcriollo.pedidos.dto.VentaResumenDto;
import com.saborcriollo.pedidos.service.ReporteService;

/** API de reportes de ventas. */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /** Ejemplo: GET /api/reportes/ventas?desde=2026-10-01&hasta=2026-10-31 */
    @GetMapping("/ventas")
    public List<VentaResumenDto> ventas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.generarReporteVentas(desde, hasta);
    }
}
