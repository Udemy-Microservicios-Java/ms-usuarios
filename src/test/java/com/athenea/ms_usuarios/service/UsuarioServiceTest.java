package com.athenea.ms_usuarios.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PoliticaRepository politicaRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private UsuarioRolRepository usuarioRolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void preparar() {
        usuarioService = new UsuarioService(
                usuarioRepository,
                politicaRepository,
                rolRepository,
                usuarioRolRepository,
                passwordEncoder,
                new UsuarioMapper());
    }

    @Test
    void crearGuardaElUsuarioConRolLector() {
        when(usuarioRepository.existsByCorreo("prueba1@athenea.com")).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario("prueba1")).thenReturn(false);
        when(politicaRepository.findById(1L)).thenReturn(Optional.of(politica()));
        when(passwordEncoder.encode("clave123")).thenReturn("hash");
        when(usuarioRepository.save(any(AbUsuario.class))).thenAnswer(invocacion -> {
            AbUsuario guardado = invocacion.getArgument(0);
            guardado.setId(5L);
            return guardado;
        });
        when(rolRepository.findByNombre("ROLE_LECTOR")).thenReturn(Optional.of(rolLector()));

        UsuarioResponse respuesta = usuarioService.crear(alta());

        assertThat(respuesta.getId()).isEqualTo(5L);
        assertThat(respuesta.getCorreo()).isEqualTo("prueba1@athenea.com");
        assertThat(respuesta.getPoliticaId()).isEqualTo(1L);
        ArgumentCaptor<AbUsuario> captor = ArgumentCaptor.forClass(AbUsuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getContrasena()).isEqualTo("hash");
        verify(usuarioRolRepository).save(any(AbUsuarioRol.class));
    }

    @Test
    void crearRechazaCorreoRepetido() {
        when(usuarioRepository.existsByCorreo("prueba1@athenea.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crear(alta()))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-047");
    }

    @Test
    void crearRechazaNombreRepetido() {
        when(usuarioRepository.existsByCorreo("prueba1@athenea.com")).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario("prueba1")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crear(alta()))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-048");
    }

    @Test
    void crearRechazaPoliticaInexistente() {
        when(usuarioRepository.existsByCorreo("prueba1@athenea.com")).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario("prueba1")).thenReturn(false);
        when(politicaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.crear(alta()))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-070");
    }

    @Test
    void crearRechazaSiNoExisteElRolLector() {
        when(usuarioRepository.existsByCorreo("prueba1@athenea.com")).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario("prueba1")).thenReturn(false);
        when(politicaRepository.findById(1L)).thenReturn(Optional.of(politica()));
        when(passwordEncoder.encode("clave123")).thenReturn("hash");
        when(usuarioRepository.save(any(AbUsuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(rolRepository.findByNombre("ROLE_LECTOR")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.crear(alta()))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-073");
    }

    @Test
    void listarDevuelveLaPagina() {
        when(usuarioRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(usuario())));

        Page<UsuarioResponse> pagina = usuarioService.listar(PageRequest.of(0, 10));

        assertThat(pagina.getContent()).hasSize(1);
        assertThat(pagina.getContent().get(0).getNombreUsuario()).isEqualTo("prueba1");
    }

    @Test
    void buscarPorIdDevuelveElUsuario() {
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario()));

        UsuarioResponse respuesta = usuarioService.buscarPorId(5L);

        assertThat(respuesta.getId()).isEqualTo(5L);
    }

    @Test
    void buscarPorIdLanzaErc049CuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-049");
    }

    @Test
    void buscarPorCorreoVacioCuandoNoExiste() {
        when(usuarioRepository.findByCorreo("nadie@athenea.com")).thenReturn(Optional.empty());

        assertThat(usuarioService.buscarPorCorreo("nadie@athenea.com")).isEmpty();
    }

    @Test
    void actualizarCambiaElNombreCompleto() {
        AbUsuario guardado = usuario();
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(guardado));
        when(politicaRepository.findById(1L)).thenReturn(Optional.of(politica()));
        when(usuarioRepository.save(any(AbUsuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        UsuarioRequest cambios = alta();
        cambios.setNombreCompleto("Usuario Editado");

        UsuarioResponse respuesta = usuarioService.actualizar(5L, cambios);

        assertThat(respuesta.getNombreCompleto()).isEqualTo("Usuario Editado");
    }

    @Test
    void actualizarRechazaCorreoDeOtroUsuario() {
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario()));
        UsuarioRequest cambios = alta();
        cambios.setCorreo("otro@athenea.com");
        when(usuarioRepository.existsByCorreo("otro@athenea.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.actualizar(5L, cambios))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-047");
    }

    @Test
    void cambiarActivoBloqueaAlUsuario() {
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario()));
        when(usuarioRepository.save(any(AbUsuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        UsuarioResponse respuesta = usuarioService.cambiarActivo(5L, false);

        assertThat(respuesta.getActivo()).isFalse();
    }

    @Test
    void eliminarBorraCuandoExiste() {
        when(usuarioRepository.existsById(5L)).thenReturn(true);

        usuarioService.eliminar(5L);

        verify(usuarioRepository).deleteById(5L);
    }

    @Test
    void eliminarLanzaErc049CuandoNoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.eliminar(99L))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-049");
        verify(usuarioRepository, never()).deleteById(99L);
    }

    private UsuarioRequest alta() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNombreUsuario("prueba1");
        request.setCorreo("prueba1@athenea.com");
        request.setContrasena("clave123");
        request.setNombreCompleto("Usuario Prueba");
        request.setFechaNacimiento(LocalDate.of(2000, 1, 15));
        request.setPoliticaId(1L);
        return request;
    }

    private AbPoliticaPrivacidad politica() {
        AbPoliticaPrivacidad politica = new AbPoliticaPrivacidad();
        politica.setId(1L);
        return politica;
    }

    private AbRol rolLector() {
        AbRol rol = new AbRol();
        rol.setId(1L);
        rol.setNombre("ROLE_LECTOR");
        return rol;
    }

    private AbUsuario usuario() {
        AbUsuario usuario = new AbUsuario();
        usuario.setId(5L);
        usuario.setNombreUsuario("prueba1");
        usuario.setCorreo("prueba1@athenea.com");
        usuario.setContrasena("hash");
        usuario.setNombreCompleto("Usuario Prueba");
        usuario.setFechaNacimiento(LocalDate.of(2000, 1, 15));
        usuario.setCorreoVerificado(false);
        usuario.setActivo(true);
        usuario.setPolitica(politica());
        return usuario;
    }
}