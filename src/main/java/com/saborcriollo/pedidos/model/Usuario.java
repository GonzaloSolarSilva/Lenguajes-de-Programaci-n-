package com.saborcriollo.pedidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Superclase abstracta de todos los usuarios del sistema.
 * La jerarquía se guarda en una sola tabla (SINGLE_TABLE) y la columna "rol"
 * indica a qué subclase pertenece cada fila.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "rol", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Usuario {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, length = 80)
    private String nombres;

    @Column(nullable = false, length = 80)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 40)
    private String username;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false)
    private boolean activo = true;

    /** Constructor requerido por JPA. */
    protected Usuario() {
    }

    protected Usuario(String nombres, String apellidos, String username, String clave) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.username = username;
        this.passwordHash = codificar(clave);
    }

    /** Genera el hash BCrypt de una contraseña en texto plano. */
    public static String codificar(String clave) {
        return ENCODER.encode(clave);
    }

    /** Verifica la contraseña ingresada contra el hash almacenado. */
    public boolean autenticar(String clave) {
        return activo && clave != null && ENCODER.matches(clave, passwordHash);
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /** Nombre del rol (coincide con el valor de la columna discriminadora). */
    public abstract String getRol();

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getUsername() {
        return username;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
