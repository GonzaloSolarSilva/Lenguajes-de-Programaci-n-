package com.saborcriollo.pedidos.exception;

/** Se lanza cuando el usuario o la contraseña son incorrectos (HTTP 401). */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos");
    }
}
