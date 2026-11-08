package com.athenea.ms_usuarios.exception;

import com.athenea.ms_usuarios.client.ExcepcionClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EscritorExcepcionTest {

    @Mock
    private ExcepcionClient excepcionClient;

    @Test
    void escribirPoneElJsonYElEstado() throws Exception {
        when(excepcionClient.buscarPorCodigo("ERC-053"))
                .thenReturn(new ExcepcionClient.Cuerpo("ERC-053", "401", "ERROR", "Debes iniciar sesión para continuar."));
        EscritorExcepcion escritor = new EscritorExcepcion(excepcionClient, new ObjectMapper());
        MockHttpServletResponse response = new MockHttpServletResponse();

        escritor.escribir(response, "ERC-053");

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("ERC-053");
    }

    @Test
    void escribirDevuelve503SiElCatalogoFalla() throws Exception {
        when(excepcionClient.buscarPorCodigo("ERC-053")).thenThrow(new RestClientException("caido"));
        EscritorExcepcion escritor = new EscritorExcepcion(excepcionClient, new ObjectMapper());
        MockHttpServletResponse response = new MockHttpServletResponse();

        escritor.escribir(response, "ERC-053");

        assertThat(response.getStatus()).isEqualTo(503);
    }
}