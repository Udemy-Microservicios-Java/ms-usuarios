package com.athenea.ms_usuarios.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExcepcionClient {

    private final RestClient restClient;

    public ExcepcionClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }

    public Cuerpo buscarPorCodigo(String codigo) {
        return restClient.get()
                .uri("/api/excepciones/{codigo}", codigo)
                .retrieve()
                .body(Cuerpo.class);
    }

    public record Cuerpo(String codigo, String codigoHttp, String tipo, String mensaje) {
        
    }
}