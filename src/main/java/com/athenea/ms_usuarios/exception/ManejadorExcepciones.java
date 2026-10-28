package com.athenea.ms_usuarios.exception;

import com.athenea.ms_usuarios.config.FiltroTraza;
import com.athenea.ms_usuarios.dto.response.RespuestaError;
import com.athenea.ms_usuarios.service.ExcepcionCatalogoService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@RestControllerAdvice
public class ManejadorExcepciones {

    private static final Logger log = LoggerFactory.getLogger(ManejadorExcepciones.class);

    private final ExcepcionCatalogoService catalogo;

    public ManejadorExcepciones(ExcepcionCatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @ExceptionHandler(ExcepcionNegocio.class)
    public ResponseEntity<RespuestaError> negocio(ExcepcionNegocio ex) {
        return catalogo.error(ex.getCodigo(), traza());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespuestaError> sinPermiso() {
        return catalogo.error(CodigosExcepcion.SIN_PERMISO, traza());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> cuerpoInvalido() {
        return catalogo.error(CodigosExcepcion.CUERPO_INVALIDO, traza());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<RespuestaError> baseDatos(DataAccessException ex) {
        log.warn("Error de base de datos");
        return catalogo.error(CodigosExcepcion.ERROR_BASE_DATOS, traza());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> inesperado(Exception ex) {
        log.error("Error no controlado", ex);
        return catalogo.error(CodigosExcepcion.ERROR_INTERNO, traza());
    }

    private String traza() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes atributos) {
            HttpServletRequest request = atributos.getRequest();
            Object valor = request.getAttribute(FiltroTraza.ATRIBUTO);
            if (valor != null) {
                return valor.toString();
            }
        }
        return UUID.randomUUID().toString();
    }
}
