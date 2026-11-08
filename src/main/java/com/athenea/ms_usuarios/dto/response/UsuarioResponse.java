package com.athenea.ms_usuarios.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nombreUsuario;
    private String correo;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private Boolean correoVerificado;
    private Boolean activo;
    private Long politicaId;
}