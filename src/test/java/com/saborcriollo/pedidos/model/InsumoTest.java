package com.saborcriollo.pedidos.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.saborcriollo.pedidos.exception.ReglaNegocioException;

class InsumoTest {

    private Insumo pescado() {
        return new Insumo("Pescado", "kg", new BigDecimal("5.000"), new BigDecimal("1.000"));
    }

    @Test
    void descontarStockReduceLasExistencias() {
        Insumo insumo = pescado();
        insumo.descontarStock(new BigDecimal("2.000"));
        assertEquals(0, new BigDecimal("3.000").compareTo(insumo.getStockActual()));
    }

    @Test
    void noSePuedeDescontarMasDeLoDisponible() {
        Insumo insumo = pescado();
        assertFalse(insumo.hayStock(new BigDecimal("6.000")));
        assertThrows(ReglaNegocioException.class, () -> insumo.descontarStock(new BigDecimal("6.000")));
    }

    @Test
    void reponerStockSumaYDetectaStockMinimo() {
        Insumo insumo = pescado();
        insumo.descontarStock(new BigDecimal("4.500"));
        assertTrue(insumo.estaBajoMinimo());
        insumo.reponerStock(new BigDecimal("10.000"));
        assertFalse(insumo.estaBajoMinimo());
    }

    @Test
    void reponerCantidadNoPositivaFalla() {
        assertThrows(ReglaNegocioException.class, () -> pescado().reponerStock(BigDecimal.ZERO));
    }
}
