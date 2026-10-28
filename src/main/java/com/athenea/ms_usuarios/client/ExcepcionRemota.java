package com.athenea.ms_usuarios.client;

public class ExcepcionRemota {

    private String codigo;
    private String codigoHttp;
    private String tipo;
    private String mensaje;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoHttp() {
        return codigoHttp;
    }

    public void setCodigoHttp(String codigoHttp) {
        this.codigoHttp = codigoHttp;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
