package com.athenea.ms_usuarios.service;

import com.athenea.ms_usuarios.dto.request.UsuarioRequest;
import com.athenea.ms_usuarios.dto.response.UsuarioResponse;
import com.athenea.ms_usuarios.entity.AbPoliticaPrivacidad;
import com.athenea.ms_usuarios.entity.AbRol;
import com.athenea.ms_usuarios.entity.AbUsuario;
import com.athenea.ms_usuarios.entity.AbUsuarioRol;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import com.athenea.ms_usuarios.repository.PoliticaRepository;
import com.athenea.ms_usuarios.repository.RolRepository;
import com.athenea.ms_usuarios.repository.UsuarioRepository;
import com.athenea.ms_usuarios.repository.UsuarioRolRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private static final String ROL_LECTOR = "ROLE_LECTOR";

    private final UsuarioRepository usuarioRepository;
    private final PoliticaRepository politicaRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                           PoliticaRepository politicaRepository,
                           RolRepository rolRepository,
                           UsuarioRolRepository usuarioRolRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.politicaRepository = politicaRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponse> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .map(this::aRespuesta);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponse> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .map(this::aRespuesta);
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
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
        return aRespuesta(guardado);
    }

    private void asignarRolLector(AbUsuario usuario) {
        AbRol rol = rolRepository.findByNombre(ROL_LECTOR)
                .orElseThrow(() -> new ExcepcionNegocio("ERC-073"));

        AbUsuarioRol usuarioRol = new AbUsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);
        usuarioRolRepository.save(usuarioRol);
    }

    private UsuarioResponse aRespuesta(AbUsuario usuario) {
        if (usuario.getPolitica() == null) {
            throw new ExcepcionNegocio("ERC-070");
        }
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getNombreCompleto(),
                usuario.getFechaNacimiento(),
                usuario.getCorreoVerificado(),
                usuario.getActivo(),
                usuario.getPolitica().getId());
    }
}