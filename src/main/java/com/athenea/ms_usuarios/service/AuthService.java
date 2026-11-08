package com.athenea.ms_usuarios.service;

import com.athenea.ms_usuarios.dto.request.LoginRequest;
import com.athenea.ms_usuarios.dto.request.UsuarioRequest;
import com.athenea.ms_usuarios.dto.response.LoginResponse;
import com.athenea.ms_usuarios.dto.response.UsuarioResponse;
import com.athenea.ms_usuarios.entity.AbUsuario;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import com.athenea.ms_usuarios.repository.UsuarioRepository;
import com.athenea.ms_usuarios.repository.UsuarioRolRepository;
import com.athenea.ms_usuarios.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthService(UsuarioRepository usuarioRepository,
                       UsuarioRolRepository usuarioRolRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    public UsuarioResponse registrar(UsuarioRequest request) {
        return usuarioService.crear(request);
    }

    @Transactional(readOnly = true)
    public LoginResponse iniciarSesion(LoginRequest request) {
        AbUsuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new ExcepcionNegocio("ERC-050"));

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new ExcepcionNegocio("ERC-050");
        }
        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new ExcepcionNegocio("ERC-051");
        }

        List<String> roles = usuarioRolRepository.findNombresByUsuarioId(usuario.getId());
        String token = jwtService.generar(usuario.getCorreo(), usuario.getId(), roles);
        return new LoginResponse(usuario.getId(), usuario.getNombreUsuario(), usuario.getCorreo(), roles, token);
    }
}