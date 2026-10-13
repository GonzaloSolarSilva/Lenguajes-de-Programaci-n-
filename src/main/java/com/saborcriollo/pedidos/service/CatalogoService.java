package com.saborcriollo.pedidos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saborcriollo.pedidos.dto.InsumoResponse;
import com.saborcriollo.pedidos.dto.MesaResponse;
import com.saborcriollo.pedidos.dto.ProductoRequest;
import com.saborcriollo.pedidos.dto.ProductoResponse;
import com.saborcriollo.pedidos.exception.RecursoNoEncontradoException;
import com.saborcriollo.pedidos.model.Categoria;
import com.saborcriollo.pedidos.model.Insumo;
import com.saborcriollo.pedidos.model.Producto;
import com.saborcriollo.pedidos.repository.CategoriaRepository;
import com.saborcriollo.pedidos.repository.InsumoRepository;
import com.saborcriollo.pedidos.repository.MesaRepository;
import com.saborcriollo.pedidos.repository.ProductoRepository;

/** Consulta y mantenimiento de mesas, carta e insumos. */
@Service
public class CatalogoService {

    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final InsumoRepository insumoRepository;

    public CatalogoService(MesaRepository mesaRepository, ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository, InsumoRepository insumoRepository) {
        this.mesaRepository = mesaRepository;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.insumoRepository = insumoRepository;
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> listarMesas() {
        return mesaRepository.findAllByOrderByNumeroAsc().stream().map(MesaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos(boolean soloDisponibles) {
        List<Producto> productos = soloDisponibles
                ? productoRepository.findByDisponibleTrueOrderByNombreAsc()
                : productoRepository.findAllByOrderByNombreAsc();
        return productos.stream().map(ProductoResponse::from).toList();
    }

    @Transactional
    public ProductoResponse crearProducto(ProductoRequest req) {
        Categoria categoria = categoriaRepository.findById(req.idCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", req.idCategoria()));
        Producto producto = new Producto(req.nombre(), req.descripcion(), req.precio(), req.estacion(), categoria);
        return ProductoResponse.from(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse cambiarDisponibilidad(Long idProducto, boolean disponible) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", idProducto));
        producto.setDisponible(disponible);
        return ProductoResponse.from(producto);
    }

    @Transactional(readOnly = true)
    public List<InsumoResponse> listarInsumos() {
        return insumoRepository.findAllByOrderByNombreAsc().stream().map(InsumoResponse::from).toList();
    }

    @Transactional
    public InsumoResponse reponerStock(Long idInsumo, java.math.BigDecimal cantidad) {
        Insumo insumo = insumoRepository.findById(idInsumo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo", idInsumo));
        insumo.reponerStock(cantidad);
        return InsumoResponse.from(insumo);
    }
}
