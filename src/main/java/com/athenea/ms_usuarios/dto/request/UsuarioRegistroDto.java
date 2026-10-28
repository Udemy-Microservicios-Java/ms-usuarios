package com.athenea.ms_usuarios.dto.request;

import java.time.LocalDate;

public class UsuarioRegistroDto {

    private String nombreUsuario;
    private String correo;
    private String contrasena;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private Long politicaId;

    public UsuarioRegistroDto() {
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getPoliticaId() {
        return politicaId;
    }

    public void setPoliticaId(Long politicaId) {
        this.politicaId = politicaId;
    }
}
