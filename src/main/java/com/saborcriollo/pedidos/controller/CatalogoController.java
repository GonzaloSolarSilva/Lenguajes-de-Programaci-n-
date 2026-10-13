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

import com.saborcriollo.pedidos.dto.DisponibilidadRequest;
import com.saborcriollo.pedidos.dto.InsumoResponse;
import com.saborcriollo.pedidos.dto.MesaResponse;
import com.saborcriollo.pedidos.dto.ProductoRequest;
import com.saborcriollo.pedidos.dto.ProductoResponse;
import com.saborcriollo.pedidos.dto.ReposicionRequest;
import com.saborcriollo.pedidos.service.CatalogoService;

import jakarta.validation.Valid;

/** API de mesas, carta de productos e inventario de insumos. */
@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/mesas")
    public List<MesaResponse> mesas() {
        return catalogoService.listarMesas();
    }

    @GetMapping("/productos")
    public List<ProductoResponse> productos(@RequestParam(defaultValue = "false") boolean soloDisponibles) {
        return catalogoService.listarProductos(soloDisponibles);
    }

    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crearProducto(@Valid @RequestBody ProductoRequest request) {
        return catalogoService.crearProducto(request);
    }

    @PatchMapping("/productos/{id}/disponibilidad")
    public ProductoResponse cambiarDisponibilidad(@PathVariable Long id,
                                                  @Valid @RequestBody DisponibilidadRequest request) {
        return catalogoService.cambiarDisponibilidad(id, request.disponible());
    }

    @GetMapping("/insumos")
    public List<InsumoResponse> insumos() {
        return catalogoService.listarInsumos();
    }

    @PostMapping("/insumos/{id}/reposicion")
    public InsumoResponse reponer(@PathVariable Long id, @Valid @RequestBody ReposicionRequest request) {
        return catalogoService.reponerStock(id, request.cantidad());
    }
}
