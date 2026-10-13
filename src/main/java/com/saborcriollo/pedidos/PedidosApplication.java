package com.saborcriollo.pedidos;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada del sistema de pedidos de Sabor Criollo. */
@SpringBootApplication
public class PedidosApplication {

    public static void main(String[] args) {
        // El restaurante opera en hora de Lima: define el turno y las fechas de los pedidos.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        SpringApplication.run(PedidosApplication.class, args);
    }
}
