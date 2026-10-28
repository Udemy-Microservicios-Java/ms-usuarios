package com.athenea.ms_usuarios.controller;

import com.athenea.ms_usuarios.dto.request.LoginRequestDto;
import com.athenea.ms_usuarios.dto.response.LoginResponseDto;
import com.athenea.ms_usuarios.dto.response.RespuestaExitosa;
import com.athenea.ms_usuarios.exception.CodigosExcepcion;
import com.athenea.ms_usuarios.service.AuthService;
import com.athenea.ms_usuarios.service.ExcepcionCatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final ExcepcionCatalogoService catalogo;

    public AuthController(AuthService authService, ExcepcionCatalogoService catalogo) {
        this.authService = authService;
        this.catalogo = catalogo;
    }

    @PostMapping("/login")
    public ResponseEntity<RespuestaExitosa<LoginResponseDto>> login(@RequestBody LoginRequestDto dto) {
        return catalogo.exito(CodigosExcepcion.LOGIN_CORRECTO, authService.login(dto));
    }
}
