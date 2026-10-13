package com.saborcriollo.pedidos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saborcriollo.pedidos.dto.AvanceRequest;
import com.saborcriollo.pedidos.dto.DetalleRequest;
import com.saborcriollo.pedidos.dto.EstadoRequest;
import com.saborcriollo.pedidos.dto.PagoRequest;
import com.saborcriollo.pedidos.dto.PagoResponse;
import com.saborcriollo.pedidos.dto.PedidoCreateRequest;
import com.saborcriollo.pedidos.dto.PedidoResponse;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;
import com.saborcriollo.pedidos.service.PedidoService;

import jakarta.validation.Valid;

/** API del ciclo de atención de pedidos. */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crear(@Valid @RequestBody PedidoCreateRequest request) {
        return pedidoService.crearPedido(request);
    }

    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) EstadoPedido estado) {
        return pedidoService.listar(estado);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id) {
        return pedidoService.obtener(id);
    }

    @PostMapping("/{id}/detalles")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse agregarDetalle(@PathVariable Long id, @Valid @RequestBody DetalleRequest request) {
        return pedidoService.agregarDetalle(id, request);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return pedidoService.cambiarEstado(id, request);
    }

    @PostMapping("/{id}/avanzar")
    public PedidoResponse avanzarEnCocina(@PathVariable Long id, @Valid @RequestBody AvanceRequest request) {
        return pedidoService.avanzarEnCocina(id, request);
    }

    @PostMapping("/{id}/pago")
    @ResponseStatus(HttpStatus.CREATED)
    public PagoResponse pagar(@PathVariable Long id, @Valid @RequestBody PagoRequest request) {
        return pedidoService.registrarPago(id, request);
    }
}
