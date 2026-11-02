package com.athenea.ms_usuarios.service;

import com.athenea.ms_usuarios.dto.request.UsuarioRequest;
import com.athenea.ms_usuarios.dto.response.UsuarioResponse;
import com.athenea.ms_usuarios.entity.AbPoliticaPrivacidad;
import com.athenea.ms_usuarios.entity.AbUsuario;
import com.athenea.ms_usuarios.repository.PoliticaRepository;
import com.athenea.ms_usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PoliticaRepository politicaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PoliticaRepository politicaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.politicaRepository = politicaRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Metodo para buscar un usuario por correo
    @Transactional(readOnly = true)
    public Optional<UsuarioResponse> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .map(this::aRespuesta);
    }

    // Metodo para buscar por nombre de usuario
    @Transactional(readOnly = true)
    public Optional<UsuarioResponse> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .map(this::aRespuesta);
    }

    // Metodo para crear un nuevo usuario
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()
                || usuarioRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }

        AbPoliticaPrivacidad politica = politicaRepository.findById(request.getPoliticaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La política no existe"));

        AbUsuario usuario = new AbUsuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setPolitica(politica);

        return aRespuesta(usuarioRepository.save(usuario));
    }

    // Metodo de Mappeo y no exponer la entidad AbUsuario directamente
    private UsuarioResponse aRespuesta(AbUsuario usuario) {
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