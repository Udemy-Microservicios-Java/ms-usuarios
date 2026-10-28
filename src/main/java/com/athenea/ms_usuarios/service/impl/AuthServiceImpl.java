package com.athenea.ms_usuarios.service.impl;

import com.athenea.ms_usuarios.dto.request.LoginRequestDto;
import com.athenea.ms_usuarios.dto.response.LoginResponseDto;
import com.athenea.ms_usuarios.entity.AbUsuario;
import com.athenea.ms_usuarios.exception.CodigosExcepcion;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import com.athenea.ms_usuarios.mapper.UsuarioMapper;
import com.athenea.ms_usuarios.repository.UsuarioRepository;
import com.athenea.ms_usuarios.service.AuthService;
import com.athenea.ms_usuarios.service.JwtService;
import com.athenea.ms_usuarios.util.Correos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final String HASH_COMPARACION =
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto dto) {
        String correo = Correos.normalizar(dto == null ? null : dto.getCorreo());
        String contrasena = dto == null ? null : dto.getContrasena();
        if (correo == null || contrasena == null || contrasena.isBlank()) {
            passwordEncoder.matches("invalida", HASH_COMPARACION);
            throw new ExcepcionNegocio(CodigosExcepcion.CREDENCIALES_INVALIDAS);
        }

        AbUsuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario == null || !passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            if (usuario == null) {
                passwordEncoder.matches(contrasena, HASH_COMPARACION);
            }
            log.warn("Inicio de sesión rechazado para {}", correo);
            throw new ExcepcionNegocio(CodigosExcepcion.CREDENCIALES_INVALIDAS);
        }
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ExcepcionNegocio(CodigosExcepcion.USUARIO_INACTIVO);
        }
        if (!Boolean.TRUE.equals(usuario.getCorreoVerificado())) {
            throw new ExcepcionNegocio(CodigosExcepcion.CORREO_NO_VERIFICADO);
        }

        String token = jwtService.generar(usuario.getCorreo(), usuario.getId());
        return new LoginResponseDto(token, "Bearer", usuarioMapper.aRespuesta(usuario));
    }
}
