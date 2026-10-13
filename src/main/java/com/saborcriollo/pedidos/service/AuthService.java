package com.saborcriollo.pedidos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saborcriollo.pedidos.dto.LoginRequest;
import com.saborcriollo.pedidos.dto.LoginResponse;
import com.saborcriollo.pedidos.exception.CredencialesInvalidasException;
import com.saborcriollo.pedidos.model.Usuario;
import com.saborcriollo.pedidos.repository.UsuarioRepository;

/** Autenticación básica por usuario y contraseña (hash BCrypt). */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByUsername(req.username())
                .filter(u -> u.autenticar(req.password()))
                .orElseThrow(CredencialesInvalidasException::new);
        return new LoginResponse(usuario.getIdUsuario(), usuario.getNombreCompleto(), usuario.getRol());
    }
}
