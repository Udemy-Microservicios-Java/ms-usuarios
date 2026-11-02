package com.athenea.ms_usuarios.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    private Long id;
    private String nombreUsuario;
    private String correo;
    private List<String> roles;
    private String token;

    public LoginResponse(Long id, String nombreUsuario, String correo, List<String> roles, String token) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.roles = roles;
        this.token = token;
    }
}