package com.athenea.ms_usuarios.service;

import com.athenea.ms_usuarios.dto.request.UsuarioRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PoliticaRepository politicaRepository,
                          RolRepository rolRepository,
                          UsuarioRolRepository usuarioRolRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.politicaRepository = politicaRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
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
        return usuarioMapper.aRespuesta(guardado);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(usuarioMapper::aRespuesta);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::aRespuesta)
                .orElseThrow(() -> new ExcepcionNegocio("ERC-049"));
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponse> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .map(usuarioMapper::aRespuesta);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponse> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .map(usuarioMapper::aRespuesta);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        AbUsuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNegocio("ERC-049"));

        if (!usuario.getCorreo().equals(request.getCorreo())
                && usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new ExcepcionNegocio("ERC-047");
        }
        if (!usuario.getNombreUsuario().equals(request.getNombreUsuario())
                && usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new ExcepcionNegocio("ERC-048");
        }

        AbPoliticaPrivacidad politica = politicaRepository.findById(request.getPoliticaId())
                .orElseThrow(() -> new ExcepcionNegocio("ERC-070"));

        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setPolitica(politica);
        return usuarioMapper.aRespuesta(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse cambiarActivo(Long id, boolean activo) {
        AbUsuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNegocio("ERC-049"));
        usuario.setActivo(activo);
        return usuarioMapper.aRespuesta(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ExcepcionNegocio("ERC-049");
        }
        usuarioRepository.deleteById(id);
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