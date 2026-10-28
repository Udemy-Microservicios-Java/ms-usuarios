package com.athenea.ms_usuarios.service;

public interface JwtService {

    String generar(String correo, Long id);

    String extraerCorreo(String token);
}
