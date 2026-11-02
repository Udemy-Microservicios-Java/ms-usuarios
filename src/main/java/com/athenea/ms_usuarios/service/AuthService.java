package com.athenea.ms_usuarios.service;

import com.athenea.ms_usuarios.dto.request.LoginRequest;
import com.athenea.ms_usuarios.dto.request.UsuarioRequest;
import com.athenea.ms_usuarios.dto.response.LoginResponse;
import com.athenea.ms_usuarios.dto.response.UsuarioResponse;
import com.athenea.ms_usuarios.entity.AbPoliticaPrivacidad;
import com.athenea.ms_usuarios.entity.AbRol;
import com.athenea.ms_usuarios.entity.AbUsuario;
import com.athenea.ms_usuarios.entity.AbUsuarioRol;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import com.athenea.ms_usuarios.mapper.UsuarioMapper;
import com.athenea.ms_usuarios.repository.PoliticaRepository;
import com.athenea.ms_usuarios.repository.RolRepository;
import com.athenea.ms_usuarios.repository.UsuarioRepository;
import com.athenea.ms_usuarios.repository.UsuarioRolRepository;
import com.athenea.ms_usuarios.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private static final String ROL_LECTOR = "ROLE_LECTOR";

    private final UsuarioRepository usuarioRepository;
    private final PoliticaRepository politicaRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
            PoliticaRepository politicaRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.politicaRepository = politicaRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioResponse registrar(UsuarioRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new ExcepcionNegocio("ERC-047");
        }
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new ExcepcionNegocio("ERC-048");
        }

        AbPoliticaPrivacidad politica = politicaRepository.findById(request.getPoliticaId())
                .orElseThrow(() -> new ExcepcionNegocio("ERC-070"));

        AbUsuario usuario = new AbUsuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setPolitica(politica);

        AbUsuario guardado = usuarioRepository.save(usuario);
        asignarRolLector(guardado);
        return usuarioMapper.aRespuesta(guardado);
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

        List<String> roles = usuarioRolRepository.findByUsuario_Id(usuario.getId()).stream()
                .map(relacion -> relacion.getRol().getNombre())
                .toList();

        String token = jwtService.generar(usuario.getCorreo(), usuario.getId());
        return new LoginResponse(usuario.getId(), usuario.getNombreUsuario(), usuario.getCorreo(), roles, token);
    }

    private void asignarRolLector(AbUsuario usuario) {
        AbRol rol = rolRepository.findByNombre(ROL_LECTOR)
                .orElseThrow(() -> new ExcepcionNegocio("ERC-073"));

        AbUsuarioRol usuarioRol = new AbUsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);
        usuarioRolRepository.save(usuarioRol);
    }
}