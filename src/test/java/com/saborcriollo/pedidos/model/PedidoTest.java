package com.saborcriollo.pedidos.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.saborcriollo.pedidos.exception.ReglaNegocioException;
import com.saborcriollo.pedidos.model.enums.Estacion;
import com.saborcriollo.pedidos.model.enums.EstadoMesa;
import com.saborcriollo.pedidos.model.enums.EstadoPedido;
import com.saborcriollo.pedidos.model.enums.MetodoPago;
import com.saborcriollo.pedidos.model.enums.Turno;

/** Pruebas de las reglas de negocio del dominio (no requieren base de datos). */
class PedidoTest {

    private Mesa mesa;
    private Mesero mesero;
    private Cajero cajero;
    private Producto ceviche;
    private Producto causa;

    @BeforeEach
    void preparar() {
        mesa = new Mesa(1, 4);
        mesero = new Mesero("Carlos", "Quispe", "mesero1", "123456", Turno.MANANA);
        cajero = new Cajero("Ana", "Torres", "cajero1", "123456");
        Categoria entradas = new Categoria("Entradas");
        ceviche = new Producto("Ceviche clásico", "", new BigDecimal("28.00"), Estacion.ENTRADAS, entradas);
        causa = new Producto("Causa limeña", "", new BigDecimal("18.00"), Estacion.ENTRADAS, entradas);
    }

    @Test
    void registrarPedidoOcupaLaMesaYEmpiezaRecibido() {
        Pedido pedido = mesero.registrarPedido(mesa);
        assertEquals(EstadoMesa.OCUPADA, mesa.getEstado());
        assertEquals(EstadoPedido.RECIBIDO, pedido.getEstado());
        assertEquals(0, BigDecimal.ZERO.compareTo(pedido.getTotal()));
    }

    @Test
    void noSePuedeOcuparUnaMesaOcupada() {
        mesero.registrarPedido(mesa);
        assertThrows(ReglaNegocioException.class, () -> mesero.registrarPedido(mesa));
    }

    @Test
    void elTotalSumaLosSubtotalesDeLosDetalles() {
        Pedido pedido = mesero.registrarPedido(mesa);
        pedido.agregarDetalle(new DetallePedido(ceviche, 2, null));
        pedido.agregarDetalle(new DetallePedido(causa, 1, "sin ají"));
        assertEquals(0, new BigDecimal("74.00").compareTo(pedido.getTotal()));
        assertEquals(2, pedido.getDetalles().size());
    }

    @Test
    void noSePuedeEnviarACocinaUnPedidoSinProductos() {
        Pedido pedido = mesero.registrarPedido(mesa);
        assertThrows(ReglaNegocioException.class, () -> pedido.cambiarEstado(EstadoPedido.EN_PREPARACION));
    }

    @Test
    void noSePermiteSaltarEstados() {
        Pedido pedido = mesero.registrarPedido(mesa);
        pedido.agregarDetalle(new DetallePedido(ceviche, 1, null));
        assertThrows(ReglaNegocioException.class, () -> pedido.cambiarEstado(EstadoPedido.SERVIDO));
    }

    @Test
    void noSeAgreganProductosDespuesDeEnviarACocina() {
        Pedido pedido = mesero.registrarPedido(mesa);
        pedido.agregarDetalle(new DetallePedido(ceviche, 1, null));
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        assertThrows(ReglaNegocioException.class,
                () -> pedido.agregarDetalle(new DetallePedido(causa, 1, null)));
    }

    @Test
    void noSeCobraUnPedidoQueNoFueServido() {
        Pedido pedido = mesero.registrarPedido(mesa);
        pedido.agregarDetalle(new DetallePedido(ceviche, 1, null));
        assertThrows(ReglaNegocioException.class, () -> cajero.cobrarPedido(pedido, MetodoPago.EFECTIVO));
    }

    @Test
    void cobrarUnPedidoServidoGeneraPagoYLiberaLaMesa() {
        Pedido pedido = mesero.registrarPedido(mesa);
        pedido.agregarDetalle(new DetallePedido(ceviche, 1, null));
        Cocinero cocinero = new Cocinero("Pedro", "Huamán", "cocinero1", "123456", Estacion.FONDOS);
        cocinero.actualizarEstado(pedido);   // EN_PREPARACION
        cocinero.actualizarEstado(pedido);   // LISTO
        pedido.cambiarEstado(EstadoPedido.SERVIDO);

        Pago pago = cajero.cobrarPedido(pedido, MetodoPago.YAPE_PLIN);

        assertEquals(EstadoPedido.PAGADO, pedido.getEstado());
        assertEquals(EstadoMesa.LIBRE, mesa.getEstado());
        assertEquals(0, new BigDecimal("28.00").compareTo(pago.getMonto()));
    }

    @Test
    void autenticarValidaLaContrasena() {
        assertTrue(mesero.autenticar("123456"));
        assertFalse(mesero.autenticar("otraClave"));
    }
}
