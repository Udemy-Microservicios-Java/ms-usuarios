package com.athenea.ms_usuarios.service;

import com.athenea.ms_usuarios.client.ExcepcionRemota;
import com.athenea.ms_usuarios.dto.response.RespuestaError;
import com.athenea.ms_usuarios.dto.response.RespuestaExitosa;
import com.athenea.ms_usuarios.exception.CodigosExcepcion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExcepcionCatalogoService {

    private static final Logger log = LoggerFactory.getLogger(ExcepcionCatalogoService.class);

    private final RestClient excepcionesRestClient;

    public ExcepcionCatalogoService(RestClient excepcionesRestClient) {
        this.excepcionesRestClient = excepcionesRestClient;
    }

    public <T> ResponseEntity<RespuestaExitosa<T>> exito(String codigo, T data) {
        ExcepcionRemota excepcion = buscar(codigo);
        int http = estado(excepcion.getCodigoHttp(), 200);
        RespuestaExitosa<T> cuerpo = new RespuestaExitosa<>(
                LocalDateTime.now(),
                String.valueOf(http),
                excepcion.getCodigo(),
                excepcion.getMensaje(),
                data);
        return ResponseEntity.status(http).body(cuerpo);
    }

    public ResponseEntity<RespuestaError> error(String codigo, String traceId) {
        ErrorPreparada preparada = preparar(codigo, traceId);
        return ResponseEntity.status(preparada.http()).body(preparada.cuerpo());
    }

    public ErrorPreparada preparar(String codigo, String traceId) {
        ExcepcionRemota excepcion = buscar(codigo);
        int http = estado(excepcion.getCodigoHttp(), 500);
        RespuestaError cuerpo = new RespuestaError(
                LocalDateTime.now(),
                nombreEstado(http),
                excepcion.getCodigo(),
                excepcion.getMensaje(),
                traceId,
                List.of());
        return new ErrorPreparada(http, cuerpo);
    }

    private ExcepcionRemota buscar(String codigo) {
        Consulta consulta = consultar(codigo);
        if (!consulta.disponible()) {
            return respaldo();
        }
        if (consulta.cuerpo() != null) {
            return consulta.cuerpo();
        }
        log.error("Código de excepción no configurado: {}", codigo);
        if (CodigosExcepcion.SERVICIO_NO_DISPONIBLE.equals(codigo)) {
            return respaldo();
        }
        Consulta servicio = consultar(CodigosExcepcion.SERVICIO_NO_DISPONIBLE);
        if (servicio.disponible() && servicio.cuerpo() != null) {
            return servicio.cuerpo();
        }
        return respaldo();
    }

    private Consulta consultar(String codigo) {
        try {
            ResponseEntity<ExcepcionRemota> respuesta = excepcionesRestClient.get()
                    .uri("/api/excepciones/{codigo}", codigo)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                    })
                    .toEntity(ExcepcionRemota.class);
            if (respuesta.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404)) || respuesta.getBody() == null) {
                return new Consulta(null, true);
            }
            return new Consulta(respuesta.getBody(), true);
        } catch (RestClientException ex) {
            log.warn("No se pudo consultar ms-excepciones para {}", codigo);
            return new Consulta(null, false);
        }
    }

    private ExcepcionRemota respaldo() {
        ExcepcionRemota fallback = new ExcepcionRemota();
        fallback.setCodigo(CodigosExcepcion.SERVICIO_NO_DISPONIBLE);
        fallback.setCodigoHttp("503");
        fallback.setTipo("ERROR");
        fallback.setMensaje("En este momento estamos presentando inconvenientes.");
        return fallback;
    }

    private int estado(String codigoHttp, int porDefecto) {
        if (codigoHttp == null || codigoHttp.isBlank()) {
            return porDefecto;
        }
        try {
            return Integer.parseInt(codigoHttp.trim());
        } catch (NumberFormatException ex) {
            return porDefecto;
        }
    }

    private String nombreEstado(int http) {
        HttpStatus estado = HttpStatus.resolve(http);
        return estado == null ? HttpStatus.INTERNAL_SERVER_ERROR.name() : estado.name();
    }

    public record ErrorPreparada(int http, RespuestaError cuerpo) {
    }

    private record Consulta(ExcepcionRemota cuerpo, boolean disponible) {
    }
}
