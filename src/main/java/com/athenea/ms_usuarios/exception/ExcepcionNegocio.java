package com.athenea.ms_usuarios.exception;

public class ExcepcionNegocio extends RuntimeException {

    private final String codigo;

    public ExcepcionNegocio(String codigo) {
        super(codigo);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
