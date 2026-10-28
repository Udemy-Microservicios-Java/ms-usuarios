package com.athenea.ms_usuarios.controller;

import com.athenea.ms_usuarios.dto.request.UsuarioActualizacionDto;
import com.athenea.ms_usuarios.dto.request.UsuarioAdminActualizacionDto;
import com.athenea.ms_usuarios.dto.request.UsuarioRegistroDto;
import com.athenea.ms_usuarios.dto.response.RespuestaExitosa;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import com.athenea.ms_usuarios.dto.response.UsuarioResponseDto;
import com.athenea.ms_usuarios.exception.CodigosExcepcion;
import com.athenea.ms_usuarios.service.ExcepcionCatalogoService;
import com.athenea.ms_usuarios.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ExcepcionCatalogoService catalogo;

    public UsuarioController(UsuarioService usuarioService, ExcepcionCatalogoService catalogo) {
        this.usuarioService = usuarioService;
        this.catalogo = catalogo;
    }

    @PostMapping
    public ResponseEntity<RespuestaExitosa<UsuarioResponseDto>> registrar(@RequestBody UsuarioRegistroDto dto) {
        return catalogo.exito(CodigosExcepcion.USUARIO_REGISTRADO, usuarioService.registrar(dto));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RespuestaExitosa<UsuarioResponseDto>> perfil(Authentication autenticacion) {
        return catalogo.exito(CodigosExcepcion.OPERACION_EXITOSA, usuarioService.perfil(correo(autenticacion)));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RespuestaExitosa<UsuarioResponseDto>> actualizarPropio(Authentication autenticacion,
                                                                              @RequestBody UsuarioActualizacionDto dto) {
        return catalogo.exito(
                CodigosExcepcion.USUARIO_ACTUALIZADO,
                usuarioService.actualizarPropio(correo(autenticacion), dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<List<UsuarioResponseDto>>> listar() {
        return catalogo.exito(CodigosExcepcion.OPERACION_EXITOSA, usuarioService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<UsuarioResponseDto>> buscarPorId(@PathVariable Long id) {
        return catalogo.exito(CodigosExcepcion.OPERACION_EXITOSA, usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<UsuarioResponseDto>> actualizar(@PathVariable Long id,
                                                                        @RequestBody UsuarioAdminActualizacionDto dto) {
        return catalogo.exito(CodigosExcepcion.USUARIO_ACTUALIZADO, usuarioService.actualizarPorAdmin(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespuestaExitosa<Void>> eliminar(@PathVariable Long id, Authentication autenticacion) {
        usuarioService.eliminar(id, correo(autenticacion));
        return catalogo.exito(CodigosExcepcion.USUARIO_ELIMINADO, null);
    }

    private String correo(Authentication autenticacion) {
        if (autenticacion == null || autenticacion.getName() == null || autenticacion.getName().isBlank()) {
            throw new ExcepcionNegocio(CodigosExcepcion.NO_AUTENTICADO);
        }
        return autenticacion.getName();
    }
}
