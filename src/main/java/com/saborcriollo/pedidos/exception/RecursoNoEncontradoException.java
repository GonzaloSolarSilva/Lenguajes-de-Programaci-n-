package com.saborcriollo.pedidos.exception;

/** Se lanza cuando no existe el registro solicitado (HTTP 404). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " con id " + id + " no encontrado(a)");
    }
}
