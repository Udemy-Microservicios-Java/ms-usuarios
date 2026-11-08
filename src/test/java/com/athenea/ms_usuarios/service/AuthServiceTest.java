package com.athenea.ms_usuarios.service;

import com.athenea.ms_usuarios.dto.request.LoginRequest;
import com.athenea.ms_usuarios.dto.response.LoginResponse;
import com.athenea.ms_usuarios.dto.response.UsuarioResponse;
import com.athenea.ms_usuarios.entity.AbUsuario;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import com.athenea.ms_usuarios.repository.UsuarioRepository;
import com.athenea.ms_usuarios.repository.UsuarioRolRepository;
import com.athenea.ms_usuarios.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioRolRepository usuarioRolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UsuarioService usuarioService;

    private AuthService authService;

    @BeforeEach
    void preparar() {
        authService = new AuthService(
                usuarioRepository,
                usuarioRolRepository,
                passwordEncoder,
                jwtService,
                usuarioService);
    }

    @Test
    void registrarDelegaEnCrear() {
        UsuarioResponse creado = new UsuarioResponse();
        creado.setId(5L);
        when(usuarioService.crear(org.mockito.ArgumentMatchers.any())).thenReturn(creado);

        UsuarioResponse respuesta = authService.registrar(new com.athenea.ms_usuarios.dto.request.UsuarioRequest());

        assertThat(respuesta.getId()).isEqualTo(5L);
    }

    @Test
    void iniciarSesionDevuelveTokenYRoles() {
        when(usuarioRepository.findByCorreo("prueba1@athenea.com")).thenReturn(Optional.of(usuarioActivo()));
        when(passwordEncoder.matches("clave123", "hash")).thenReturn(true);
        when(usuarioRolRepository.findNombresByUsuarioId(5L)).thenReturn(List.of("ROLE_LECTOR"));
        when(jwtService.generar("prueba1@athenea.com", 5L, List.of("ROLE_LECTOR"))).thenReturn("token-prueba");

        LoginResponse respuesta = authService.iniciarSesion(login("clave123"));

        assertThat(respuesta.getToken()).isEqualTo("token-prueba");
        assertThat(respuesta.getRoles()).containsExactly("ROLE_LECTOR");
    }

    @Test
    void iniciarSesionRechazaCorreoDesconocido() {
        when(usuarioRepository.findByCorreo("prueba1@athenea.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.iniciarSesion(login("clave123")))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-050");
    }

    @Test
    void iniciarSesionRechazaClaveIncorrecta() {
        when(usuarioRepository.findByCorreo("prueba1@athenea.com")).thenReturn(Optional.of(usuarioActivo()));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.iniciarSesion(login("mala")))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-050");
    }

    @Test
    void iniciarSesionRechazaUsuarioBloqueado() {
        AbUsuario bloqueado = usuarioActivo();
        bloqueado.setActivo(false);
        when(usuarioRepository.findByCorreo("prueba1@athenea.com")).thenReturn(Optional.of(bloqueado));
        when(passwordEncoder.matches("clave123", "hash")).thenReturn(true);

        assertThatThrownBy(() -> authService.iniciarSesion(login("clave123")))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-051");
    }

    private LoginRequest login(String contrasena) {
        LoginRequest request = new LoginRequest();
        request.setCorreo("prueba1@athenea.com");
        request.setContrasena(contrasena);
        return request;
    }

    private AbUsuario usuarioActivo() {
        AbUsuario usuario = new AbUsuario();
        usuario.setId(5L);
        usuario.setNombreUsuario("prueba1");
        usuario.setCorreo("prueba1@athenea.com");
        usuario.setContrasena("hash");
        usuario.setActivo(true);
        return usuario;
    }
}