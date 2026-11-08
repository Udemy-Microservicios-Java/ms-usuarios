package com.athenea.ms_usuarios.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ExcepcionClientTest {

    @Test
    void buscarPorCodigoLeeElCuerpo() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://excepciones");
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();
        servidor.expect(requestTo("http://excepciones/api/excepciones/ERC-047"))
                .andRespond(withSuccess("""
                        {"codigo":"ERC-047","codigoHttp":"409","tipo":"ERROR","mensaje":"El correo ya está registrado."}
                        """, MediaType.APPLICATION_JSON));

        ExcepcionClient.Cuerpo cuerpo = new ExcepcionClient(builder.build()).buscarPorCodigo("ERC-047");

        assertThat(cuerpo.codigo()).isEqualTo("ERC-047");
        assertThat(cuerpo.codigoHttp()).isEqualTo("409");
        servidor.verify();
    }
}