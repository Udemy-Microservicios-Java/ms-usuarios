package com.athenea.ms_usuarios.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderConfigTest {

    @Test
    void elBeanCifraYReconoceLaClave() {
        PasswordEncoder encoder = new PasswordEncoderConfig().passwordEncoder();
        String hash = encoder.encode("clave123");

        assertThat(encoder.matches("clave123", hash)).isTrue();
        assertThat(encoder.matches("otra", hash)).isFalse();
    }
}