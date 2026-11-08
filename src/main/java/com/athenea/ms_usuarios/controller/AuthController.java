package com.athenea.ms_usuarios.controller;

import com.athenea.ms_usuarios.dto.request.LoginRequest;
import com.athenea.ms_usuarios.dto.request.UsuarioRequest;
import com.athenea.ms_usuarios.dto.response.LoginResponse;
import com.athenea.ms_usuarios.dto.response.UsuarioResponse;
import com.athenea.ms_usuarios.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody UsuarioRequest request) {
        UsuarioResponse creado = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/login")
    public LoginResponse iniciarSesion(@RequestBody LoginRequest request) {
        return authService.iniciarSesion(request);
    }
}