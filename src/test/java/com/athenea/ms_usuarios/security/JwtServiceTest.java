package com.athenea.ms_usuarios.security;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "AtheneaLocal2026-ClaveJwt-SoloEnTuMaquina-32", 3600);

    @Test
    void generarYLeerConservaCorreoIdYRoles() {
        String token = jwtService.generar("admin@athenea.com", 1L, List.of("ROLE_ADMIN"));

        assertThat(jwtService.leer(token).getSubject()).isEqualTo("admin@athenea.com");
        assertThat(jwtService.leer(token).get("uid", Long.class)).isEqualTo(1L);
        assertThat(jwtService.leer(token).get("roles", List.class)).containsExactly("ROLE_ADMIN");
    }

    @Test
    void leerRechazaUnTokenInvalido() {
        assertThatThrownBy(() -> jwtService.leer("esto-no-es-un-token"))
                .isInstanceOf(JwtException.class);
    }
}