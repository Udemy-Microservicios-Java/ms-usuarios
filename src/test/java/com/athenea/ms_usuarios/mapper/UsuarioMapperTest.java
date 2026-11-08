package com.athenea.ms_usuarios.mapper;

import com.athenea.ms_usuarios.dto.response.UsuarioResponse;
import com.athenea.ms_usuarios.entity.AbPoliticaPrivacidad;
import com.athenea.ms_usuarios.entity.AbUsuario;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    void aRespuestaCopiaLosDatosSinLaContrasena() {
        AbPoliticaPrivacidad politica = new AbPoliticaPrivacidad();
        politica.setId(1L);
        AbUsuario usuario = new AbUsuario();
        usuario.setId(5L);
        usuario.setNombreUsuario("prueba1");
        usuario.setCorreo("prueba1@athenea.com");
        usuario.setNombreCompleto("Usuario Prueba");
        usuario.setFechaNacimiento(LocalDate.of(2000, 1, 15));
        usuario.setCorreoVerificado(false);
        usuario.setActivo(true);
        usuario.setPolitica(politica);

        UsuarioResponse respuesta = mapper.aRespuesta(usuario);

        assertThat(respuesta.getId()).isEqualTo(5L);
        assertThat(respuesta.getPoliticaId()).isEqualTo(1L);
    }

    @Test
    void aRespuestaLanzaErc070SiNoHayPolitica() {
        assertThatThrownBy(() -> mapper.aRespuesta(new AbUsuario()))
                .isInstanceOf(ExcepcionNegocio.class)
                .extracting("codigo")
                .isEqualTo("ERC-070");
    }
}