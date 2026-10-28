package com.athenea.ms_usuarios.dto.request;

public class UsuarioAdminActualizacionDto {

    private String nombreCompleto;
    private Boolean activo;

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
