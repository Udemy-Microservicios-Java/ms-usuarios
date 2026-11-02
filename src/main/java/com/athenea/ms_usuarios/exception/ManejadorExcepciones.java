package com.athenea.ms_usuarios.exception;

import com.athenea.ms_usuarios.client.ExcepcionClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class ManejadorExcepciones {

    private final ExcepcionClient excepcionClient;

    public ManejadorExcepciones(ExcepcionClient excepcionClient) {
        this.excepcionClient = excepcionClient;
    }

    @ExceptionHandler(ExcepcionNegocio.class)
    public ResponseEntity<ExcepcionClient.Cuerpo> manejar(ExcepcionNegocio ex) {
        return responder(ex.getCodigo());
    }

    public ResponseEntity<ExcepcionClient.Cuerpo> responder(String codigo) {
        try {
            ExcepcionClient.Cuerpo cuerpo = excepcionClient.buscarPorCodigo(codigo);
            return ResponseEntity.status(estado(cuerpo.codigoHttp())).body(cuerpo);
        } catch (RestClientException error) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    private HttpStatus estado(String codigoHttp) {
        try {
            HttpStatus status = HttpStatus.resolve(Integer.parseInt(codigoHttp));
            return status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
        } catch (NumberFormatException error) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}