package com.athenea.ms_usuarios.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntidadesTest {

    @Test
    void usuarioNuevoQuedaActivoYSinCorreoVerificado() {
        AbUsuario usuario = new AbUsuario();

        usuario.prePersist();

        assertThat(usuario.getActivo()).isTrue();
        assertThat(usuario.getCorreoVerificado()).isFalse();
        assertThat(usuario.getFechaRegistro()).isNotNull();
        usuario.preUpdate();
        assertThat(usuario.getFechaActualizacion()).isNotNull();
    }

    @Test
    void rolYPoliticaGuardanLaFecha() {
        AbRol rol = new AbRol();
        rol.prePersist();
        rol.preUpdate();
        AbPoliticaPrivacidad politica = new AbPoliticaPrivacidad();
        politica.prePersist();
        politica.preUpdate();

        assertThat(rol.getFechaRegistro()).isNotNull();
        assertThat(politica.getFechaActualizacion()).isNotNull();
    }

    @Test
    void relacionUsuarioRolGuardaLaFechaYComparaSuId() {
        AbUsuarioRol relacion = new AbUsuarioRol();
        relacion.prePersist();
        AbUsuarioRol.UsuarioRolId uno = new AbUsuarioRol.UsuarioRolId();
        uno.setUsuarioId(1L);
        uno.setRolId(2L);
        AbUsuarioRol.UsuarioRolId igual = new AbUsuarioRol.UsuarioRolId();
        igual.setUsuarioId(1L);
        igual.setRolId(2L);
        AbUsuarioRol.UsuarioRolId otro = new AbUsuarioRol.UsuarioRolId();
        otro.setUsuarioId(9L);
        otro.setRolId(2L);

        assertThat(relacion.getFechaRegistro()).isNotNull();
        assertThat(uno).isEqualTo(igual);
        assertThat(uno).hasSameHashCodeAs(igual);
        assertThat(uno).isNotEqualTo(otro);
        assertThat(uno).isNotEqualTo("otro tipo");
    }
}