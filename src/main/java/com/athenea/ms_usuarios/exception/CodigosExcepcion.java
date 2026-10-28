package com.athenea.ms_usuarios.exception;

public final class CodigosExcepcion {

    public static final String NOMBRE_OBLIGATORIO = "ERC-040";
    public static final String CORREO_OBLIGATORIO = "ERC-041";
    public static final String CORREO_FORMATO = "ERC-042";
    public static final String CONTRASENA_OBLIGATORIA = "ERC-043";
    public static final String CONTRASENA_CORTA = "ERC-044";
    public static final String FECHA_NACIMIENTO_OBLIGATORIA = "ERC-045";
    public static final String POLITICA_OBLIGATORIA = "ERC-046";
    public static final String CORREO_DUPLICADO = "ERC-047";
    public static final String USUARIO_DUPLICADO = "ERC-048";
    public static final String USUARIO_NO_EXISTE = "ERC-049";
    public static final String CREDENCIALES_INVALIDAS = "ERC-050";
    public static final String USUARIO_INACTIVO = "ERC-051";
    public static final String CORREO_NO_VERIFICADO = "ERC-052";
    public static final String POLITICA_NO_EXISTE = "ERC-070";
    public static final String POLITICA_NO_VIGENTE = "ERC-071";
    public static final String ROL_NO_EXISTE = "ERC-073";
    public static final String NO_AUTENTICADO = "ERC-074";
    public static final String SIN_PERMISO = "ERC-075";

    public static final String OPERACION_EXITOSA = "EXT-003";
    public static final String USUARIO_REGISTRADO = "EXT-004";
    public static final String USUARIO_ACTUALIZADO = "EXT-005";
    public static final String USUARIO_ELIMINADO = "EXT-006";
    public static final String LOGIN_CORRECTO = "EXT-007";
    public static final String SERVICIO_NO_DISPONIBLE = "ERS-011";
    public static final String CUERPO_INVALIDO = "BR-001";
    public static final String ERROR_INTERNO = "ISR-001";
    public static final String ERROR_BASE_DATOS = "ISR-002";

    private CodigosExcepcion() {
    }
}
