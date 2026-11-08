package com.athenea.ms_usuarios.dto.request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioRequest {

    private String nombreUsuario;
    private String correo;
    private String contrasena;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private Long politicaId;
}