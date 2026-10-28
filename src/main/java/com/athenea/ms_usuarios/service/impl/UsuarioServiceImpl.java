package com.athenea.ms_usuarios.service.impl;

import com.athenea.ms_usuarios.dto.request.UsuarioActualizacionDto;
import com.athenea.ms_usuarios.dto.request.UsuarioAdminActualizacionDto;
import com.athenea.ms_usuarios.dto.request.UsuarioRegistroDto;
import com.athenea.ms_usuarios.dto.response.UsuarioResponseDto;
import com.athenea.ms_usuarios.entity.AbPoliticaPrivacidad;
import com.athenea.ms_usuarios.entity.AbRol;
import com.athenea.ms_usuarios.entity.AbUsuario;
import com.athenea.ms_usuarios.exception.CodigosExcepcion;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import com.athenea.ms_usuarios.mapper.UsuarioMapper;
import com.athenea.ms_usuarios.repository.PoliticaPrivacidadRepository;
import com.athenea.ms_usuarios.repository.RolRepository;
import com.athenea.ms_usuarios.repository.UsuarioRepository;
import com.athenea.ms_usuarios.service.UsuarioService;
import com.athenea.ms_usuarios.util.Correos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final String ROL_LECTOR = "ROLE_LECTOR";
    private static final int MIN_CONTRASENA = 8;
    private static final int MAX_CONTRASENA = 72;

    private final UsuarioRepository usuarioRepository;
    private final PoliticaPrivacidadRepository politicaPrivacidadRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PoliticaPrivacidadRepository politicaPrivacidadRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder,
                              UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.politicaPrivacidadRepository = politicaPrivacidadRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional
    public UsuarioResponseDto registrar(UsuarioRegistroDto dto) {
        validarRegistro(dto);

        String nombreUsuario = dto.getNombreUsuario().trim();
        String correo = Correos.normalizar(dto.getCorreo());

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ExcepcionNegocio(CodigosExcepcion.CORREO_DUPLICADO);
        }
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new ExcepcionNegocio(CodigosExcepcion.USUARIO_DUPLICADO);
        }

        AbPoliticaPrivacidad politica = politicaPrivacidadRepository.findById(dto.getPoliticaId())
                .orElseThrow(() -> new ExcepcionNegocio(CodigosExcepcion.POLITICA_NO_EXISTE));
        if (!Boolean.TRUE.equals(politica.getVigente())) {
            throw new ExcepcionNegocio(CodigosExcepcion.POLITICA_NO_VIGENTE);
        }

        AbRol lector = rolRepository.findByNombre(ROL_LECTOR)
                .orElseThrow(() -> new ExcepcionNegocio(CodigosExcepcion.ROL_NO_EXISTE));

        AbUsuario usuario = new AbUsuario();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setNombreCompleto(limpiarNombre(dto.getNombreCompleto()));
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setPoliticaPrivacidad(politica);
        usuario.setCorreoVerificado(false);
        usuario.setActivo(true);
        Set<AbRol> roles = new HashSet<>();
        roles.add(lector);
        usuario.setRoles(roles);

        AbUsuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado con id {}", guardado.getId());
        return usuarioMapper.aRespuesta(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listar() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::aRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto buscarPorId(Long id) {
        return usuarioMapper.aRespuesta(buscarPorIdEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto perfil(String correoAutenticado) {
        return usuarioMapper.aRespuesta(buscarCuentaOperativa(correoAutenticado));
    }

    @Override
    @Transactional
    public UsuarioResponseDto actualizarPropio(String correoAutenticado, UsuarioActualizacionDto dto) {
        if (dto == null) {
            throw new ExcepcionNegocio(CodigosExcepcion.NOMBRE_OBLIGATORIO);
        }
        AbUsuario usuario = buscarCuentaOperativa(correoAutenticado);
        if (dto.getNombreCompleto() != null) {
            usuario.setNombreCompleto(limpiarNombre(dto.getNombreCompleto()));
        }
        if (dto.getContrasenaNueva() != null && !dto.getContrasenaNueva().isBlank()) {
            if (dto.getContrasenaActual() == null
                    || !passwordEncoder.matches(dto.getContrasenaActual(), usuario.getContrasena())) {
                throw new ExcepcionNegocio(CodigosExcepcion.CREDENCIALES_INVALIDAS);
            }
            validarContrasena(dto.getContrasenaNueva());
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasenaNueva()));
        }
        return usuarioMapper.aRespuesta(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponseDto actualizarPorAdmin(Long id, UsuarioAdminActualizacionDto dto) {
        if (dto == null) {
            throw new ExcepcionNegocio(CodigosExcepcion.NOMBRE_OBLIGATORIO);
        }
        AbUsuario usuario = buscarPorIdEntidad(id);
        if (dto.getNombreCompleto() != null) {
            usuario.setNombreCompleto(limpiarNombre(dto.getNombreCompleto()));
        }
        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }
        return usuarioMapper.aRespuesta(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminar(Long id, String correoAutenticado) {
        AbUsuario usuario = buscarPorIdEntidad(id);
        if (usuario.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new ExcepcionNegocio(CodigosExcepcion.SIN_PERMISO);
        }
        usuarioRepository.delete(usuario);
    }

    private AbUsuario buscarPorIdEntidad(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ExcepcionNegocio(CodigosExcepcion.USUARIO_NO_EXISTE));
    }

    private AbUsuario buscarCuentaOperativa(String correoAutenticado) {
        String correo = Correos.normalizar(correoAutenticado);
        AbUsuario usuario = usuarioRepository.findByCorreo(correo == null ? "" : correo)
                .orElseThrow(() -> new ExcepcionNegocio(CodigosExcepcion.USUARIO_NO_EXISTE));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ExcepcionNegocio(CodigosExcepcion.USUARIO_INACTIVO);
        }
        if (!Boolean.TRUE.equals(usuario.getCorreoVerificado())) {
            throw new ExcepcionNegocio(CodigosExcepcion.CORREO_NO_VERIFICADO);
        }
        return usuario;
    }

    private void validarRegistro(UsuarioRegistroDto dto) {
        if (dto == null || dto.getNombreUsuario() == null || dto.getNombreUsuario().isBlank()) {
            throw new ExcepcionNegocio(CodigosExcepcion.NOMBRE_OBLIGATORIO);
        }
        if (dto.getNombreUsuario().trim().length() > 50) {
            throw new ExcepcionNegocio(CodigosExcepcion.NOMBRE_OBLIGATORIO);
        }
        String correo = Correos.normalizar(dto.getCorreo());
        if (correo == null) {
            throw new ExcepcionNegocio(CodigosExcepcion.CORREO_OBLIGATORIO);
        }
        if (correo.length() > 100 || !CORREO.matcher(correo).matches()) {
            throw new ExcepcionNegocio(CodigosExcepcion.CORREO_FORMATO);
        }
        validarContrasena(dto.getContrasena());
        if (dto.getFechaNacimiento() == null) {
            throw new ExcepcionNegocio(CodigosExcepcion.FECHA_NACIMIENTO_OBLIGATORIA);
        }
        if (dto.getPoliticaId() == null) {
            throw new ExcepcionNegocio(CodigosExcepcion.POLITICA_OBLIGATORIA);
        }
        if (dto.getNombreCompleto() != null && dto.getNombreCompleto().trim().length() > 120) {
            throw new ExcepcionNegocio(CodigosExcepcion.NOMBRE_OBLIGATORIO);
        }
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new ExcepcionNegocio(CodigosExcepcion.CONTRASENA_OBLIGATORIA);
        }
        if (contrasena.length() < MIN_CONTRASENA || contrasena.length() > MAX_CONTRASENA) {
            throw new ExcepcionNegocio(CodigosExcepcion.CONTRASENA_CORTA);
        }
    }

    private String limpiarNombre(String nombreCompleto) {
        if (nombreCompleto == null) {
            return null;
        }
        String limpio = nombreCompleto.trim();
        if (limpio.length() > 120) {
            throw new ExcepcionNegocio(CodigosExcepcion.NOMBRE_OBLIGATORIO);
        }
        return limpio.isEmpty() ? null : limpio;
    }
}
