package com.athenea.ms_usuarios.controller;

import com.athenea.ms_usuarios.client.ExcepcionClient;
import com.athenea.ms_usuarios.dto.response.LoginResponse;
import com.athenea.ms_usuarios.dto.response.UsuarioResponse;
import com.athenea.ms_usuarios.exception.EscritorExcepcion;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import com.athenea.ms_usuarios.exception.ManejadorExcepciones;
import com.athenea.ms_usuarios.security.JwtAuthenticationFilter;
import com.athenea.ms_usuarios.security.JwtService;
import com.athenea.ms_usuarios.security.SecurityConfig;
import com.athenea.ms_usuarios.service.AuthService;
import com.athenea.ms_usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UsuarioController.class, AuthController.class})
@Import({ActivarSeguridadWeb.class, SecurityFilterAutoConfiguration.class,
        SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class,
        EscritorExcepcion.class, ManejadorExcepciones.class})
@TestPropertySource(properties = {
        "jwt.secret=AtheneaLocal2026-ClaveJwt-SoloEnTuMaquina-32",
        "jwt.expiracion-segundos=3600"
})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private ExcepcionClient excepcionClient;

    @BeforeEach
    void catalogo() {
        when(excepcionClient.buscarPorCodigo(anyString())).thenAnswer(invocacion -> {
            String codigo = invocacion.getArgument(0);
            String http = switch (codigo) {
                case "ERC-053" -> "401";
                case "ERC-054" -> "403";
                case "ERC-049" -> "404";
                case "EXT-006" -> "200";
                default -> "500";
            };
            return new ExcepcionClient.Cuerpo(codigo, http, "ERROR", codigo);
        });
    }

    @Test
    void loginYRegistroSonPublicos() throws Exception {
        when(authService.iniciarSesion(any())).thenReturn(
                new LoginResponse(1L, "admin", "admin@athenea.com", List.of("ROLE_ADMIN"), "token-respuesta"));
        when(authService.registrar(any())).thenReturn(usuario());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@athenea.com\",\"contrasena\":\"clave123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-respuesta"));

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void sinTokenLasRutasDeUsuariosResponden401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("ERC-053"));
    }

    @Test
    void lectorNoPuedeListarNiBorrar() throws Exception {
        String lector = token("ROLE_LECTOR");

        mockMvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + lector))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("ERC-054"));

        mockMvc.perform(delete("/api/usuarios/5").header("Authorization", "Bearer " + lector))
                .andExpect(status().isForbidden());
    }

    @Test
    void administradorListaBuscaCreaActualizaBloqueaYBorra() throws Exception {
        String admin = token("ROLE_ADMIN");
        when(usuarioService.listar(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(usuario())));
        when(usuarioService.buscarPorId(5L)).thenReturn(usuario());
        when(usuarioService.buscarPorCorreo("prueba1@athenea.com")).thenReturn(Optional.of(usuario()));
        when(usuarioService.buscarPorNombreUsuario("prueba1")).thenReturn(Optional.of(usuario()));
        when(usuarioService.crear(any())).thenReturn(usuario());
        when(usuarioService.actualizar(anyLong(), any())).thenReturn(usuario());
        when(usuarioService.cambiarActivo(anyLong(), anyBoolean())).thenReturn(usuario());

        mockMvc.perform(get("/api/usuarios?page=0&size=10").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].correo").value("prueba1@athenea.com"));
        mockMvc.perform(get("/api/usuarios/5").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/usuarios/correo").param("correo", "prueba1@athenea.com")
                        .header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/usuarios/nombre/prueba1").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/usuarios").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUsuario()))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/usuarios/5").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUsuario()))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/usuarios/5/activo").param("activo", "false")
                        .header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/usuarios/5").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("EXT-006"));
    }

    @Test
    void bibliotecarioActualizaYNoBorra() throws Exception {
        String bibliotecario = token("ROLE_BIBLIOTECARIO");
        when(usuarioService.actualizar(anyLong(), any())).thenReturn(usuario());

        mockMvc.perform(put("/api/usuarios/5").header("Authorization", "Bearer " + bibliotecario)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUsuario()))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/usuarios/5").header("Authorization", "Bearer " + bibliotecario))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("ERC-054"));
    }

    @Test
    void usuarioInexistenteRespondeConErc049() throws Exception {
        when(usuarioService.buscarPorId(99L)).thenThrow(new ExcepcionNegocio("ERC-049"));

        mockMvc.perform(get("/api/usuarios/99").header("Authorization", "Bearer " + token("ROLE_ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("ERC-049"));
    }

    private String token(String rol) {
        return jwtService.generar("persona@athenea.com", 1L, List.of(rol));
    }

    private UsuarioResponse usuario() {
        return new UsuarioResponse(5L, "prueba1", "prueba1@athenea.com", "Usuario Prueba",
                LocalDate.of(2000, 1, 15), false, true, 1L);
    }

    private String jsonUsuario() {
        return """
                {"nombreUsuario":"prueba1","correo":"prueba1@athenea.com","contrasena":"clave123",
                "nombreCompleto":"Usuario Prueba","fechaNacimiento":"2000-01-15","politicaId":1}
                """;
    }
}

@Configuration
@EnableWebSecurity
class ActivarSeguridadWeb {
}