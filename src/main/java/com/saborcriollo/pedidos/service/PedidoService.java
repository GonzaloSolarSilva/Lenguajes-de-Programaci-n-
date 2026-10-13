package com.saborcriollo.pedidos.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saborcriollo.pedidos.dto.AvanceRequest;
import com.saborcriollo.pedidos.dto.DetalleRequest;
import com.saborcriollo.pedidos.dto.EstadoRequest;
import com.saborcriollo.pedidos.dto.PagoRequest;
import com.saborcriollo.pedidos.dto.PagoResponse;
import com.saborcriollo.pedidos.dto.PedidoCreateRequest;
import com.saborcriollo.pedidos.dto.PedidoResponse;
import com.saborcriollo.pedidos.exception.RecursoNoEncontradoException;
import com.saborcriollo.pedidos.exception.ReglaNegocioException;
import com.saborcriollo.pedidos.model.Cajero;
import com.saborcriollo.pedidos.model.Cocinero;
import com.saborcriollo.pedidos.model.DetallePedido;
import com.saborcriollo.pedidos.model.Mesa;
import com.saborcriollo.pedidos.model.Mesero;
import com.saborcriollo.pedidos.model.Pago;
import com.saborcriollo.pedidos.model.Pedido;
import com.saborcriollo.pedidos.model.Producto;
import com.saborcriollo.pedidos.model.RecetaItem;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;
import com.saborcriollo.pedidos.repository.CajeroRepository;
import com.saborcriollo.pedidos.repository.ClienteRepository;
import com.saborcriollo.pedidos.repository.CocineroRepository;
import com.saborcriollo.pedidos.repository.MesaRepository;
import com.saborcriollo.pedidos.repository.MeseroRepository;
import com.saborcriollo.pedidos.repository.PagoRepository;
import com.saborcriollo.pedidos.repository.PedidoRepository;
import com.saborcriollo.pedidos.repository.ProductoRepository;
import com.saborcriollo.pedidos.repository.RecetaItemRepository;

/** Casos de uso del ciclo de atención: tomar pedido, enviar a cocina, servir y cobrar. */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final MeseroRepository meseroRepository;
    private final CocineroRepository cocineroRepository;
    private final CajeroRepository cajeroRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final RecetaItemRepository recetaRepository;
    private final PagoRepository pagoRepository;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository,
                         MeseroRepository meseroRepository, CocineroRepository cocineroRepository,
                         CajeroRepository cajeroRepository, ClienteRepository clienteRepository,
                         ProductoRepository productoRepository, RecetaItemRepository recetaRepository,
                         PagoRepository pagoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.meseroRepository = meseroRepository;
        this.cocineroRepository = cocineroRepository;
        this.cajeroRepository = cajeroRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.recetaRepository = recetaRepository;
        this.pagoRepository = pagoRepository;
    }

    /** Abre un pedido para una mesa y la marca como ocupada. */
    @Transactional
    public PedidoResponse crearPedido(PedidoCreateRequest req) {
        Mesa mesa = mesaRepository.findById(req.idMesa())
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa", req.idMesa()));
        Mesero mesero = meseroRepository.findById(req.idMesero())
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesero", req.idMesero()));

        Pedido pedido = mesero.registrarPedido(mesa);
        if (req.idCliente() != null) {
            pedido.setCliente(clienteRepository.findById(req.idCliente())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", req.idCliente())));
        }
        return PedidoResponse.from(pedidoRepository.saveAndFlush(pedido));
    }

    /** Agrega un producto al pedido, validando disponibilidad y stock de insumos. */
    @Transactional
    public PedidoResponse agregarDetalle(Long idPedido, DetalleRequest req) {
        Pedido pedido = buscarPedido(idPedido);
        pedido.verificarModificable();

        Producto producto = productoRepository.findById(req.idProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", req.idProducto()));
        if (!producto.verificarDisponibilidad()) {
            throw new ReglaNegocioException("El producto \"" + producto.getNombre() + "\" no está disponible");
        }
        validarStock(producto, req.cantidad());

        pedido.agregarDetalle(new DetallePedido(producto, req.cantidad(), req.observacion()));
        return PedidoResponse.from(pedidoRepository.saveAndFlush(pedido));
    }

    /** Cambia el estado del pedido (SERVIDO, CANCELADO, etc.). El pago se registra aparte. */
    @Transactional
    public PedidoResponse cambiarEstado(Long idPedido, EstadoRequest req) {
        if (req.estado() == EstadoPedido.PAGADO) {
            throw new ReglaNegocioException("El estado PAGADO solo se asigna al registrar el pago");
        }
        Pedido pedido = buscarPedido(idPedido);
        pedido.cambiarEstado(req.estado());
        if (req.estado() == EstadoPedido.CANCELADO) {
            pedido.getMesa().liberar();
        }
        return PedidoResponse.from(pedido);
    }

    /** Cocina avanza el pedido un paso: RECIBIDO -> EN_PREPARACION -> LISTO. */
    @Transactional
    public PedidoResponse avanzarEnCocina(Long idPedido, AvanceRequest req) {
        Cocinero cocinero = cocineroRepository.findById(req.idCocinero())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cocinero", req.idCocinero()));
        Pedido pedido = buscarPedido(idPedido);
        cocinero.actualizarEstado(pedido);
        return PedidoResponse.from(pedido);
    }

    /** Cobra el pedido, descuenta el stock de insumos y libera la mesa. */
    @Transactional
    public PagoResponse registrarPago(Long idPedido, PagoRequest req) {
        Pedido pedido = buscarPedido(idPedido);
        Cajero cajero = cajeroRepository.findById(req.idCajero())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cajero", req.idCajero()));

        Pago pago = cajero.cobrarPedido(pedido, req.metodoPago());
        descontarInsumos(pedido);
        return PagoResponse.from(pagoRepository.save(pago));
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtener(Long idPedido) {
        return PedidoResponse.from(buscarPedido(idPedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(EstadoPedido estado) {
        List<Pedido> pedidos = (estado == null)
                ? pedidoRepository.findAllByOrderByFechaHoraDesc()
                : pedidoRepository.findByEstadoOrderByFechaHoraAsc(estado);
        return pedidos.stream().map(PedidoResponse::from).toList();
    }

    // ------------------------------------------------------------------ privados

    private Pedido buscarPedido(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", idPedido));
    }

    /** Evita ofrecer platos sin insumos suficientes (problema 4 del Capítulo I). */
    private void validarStock(Producto producto, int cantidad) {
        for (RecetaItem item : recetaRepository.buscarPorProducto(producto.getIdProducto())) {
            BigDecimal requerido = item.getCantidad().multiply(BigDecimal.valueOf(cantidad));
            if (!item.getInsumo().hayStock(requerido)) {
                throw new ReglaNegocioException("No hay stock suficiente de " + item.getInsumo().getNombre()
                        + " para preparar " + cantidad + " x " + producto.getNombre());
            }
        }
    }

    private void descontarInsumos(Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            for (RecetaItem item : recetaRepository.buscarPorProducto(detalle.getProducto().getIdProducto())) {
                BigDecimal requerido = item.getCantidad().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                item.getInsumo().descontarStock(requerido);
            }
        }
    }
}
