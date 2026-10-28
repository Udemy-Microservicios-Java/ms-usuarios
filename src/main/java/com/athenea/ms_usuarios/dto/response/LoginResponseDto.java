package com.athenea.ms_usuarios.dto.response;

public class LoginResponseDto {

    private String token;
    private String tipo;
    private UsuarioResponseDto usuario;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, String tipo, UsuarioResponseDto usuario) {
        this.token = token;
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public UsuarioResponseDto getUsuario() {
        return usuario;
    }
}
