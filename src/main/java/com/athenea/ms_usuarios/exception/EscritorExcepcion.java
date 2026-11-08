package com.athenea.ms_usuarios.exception;

import com.athenea.ms_usuarios.client.ExcepcionClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class EscritorExcepcion {

    private final ExcepcionClient excepcionClient;
    private final ObjectMapper objectMapper;

    public EscritorExcepcion(ExcepcionClient excepcionClient, ObjectMapper objectMapper) {
        this.excepcionClient = excepcionClient;
        this.objectMapper = objectMapper;
    }

    public void escribir(HttpServletResponse response, String codigo) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            ExcepcionClient.Cuerpo cuerpo = excepcionClient.buscarPorCodigo(codigo);
            response.setStatus(Integer.parseInt(cuerpo.codigoHttp()));
            objectMapper.writeValue(response.getWriter(), cuerpo);
        } catch (RestClientException | NumberFormatException error) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        }
    }
}