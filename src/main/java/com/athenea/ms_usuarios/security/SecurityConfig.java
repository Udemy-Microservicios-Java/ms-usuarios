package com.athenea.ms_usuarios.security;

import com.athenea.ms_usuarios.exception.EscritorExcepcion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final EscritorExcepcion escritorExcepcion;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          EscritorExcepcion escritorExcepcion) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.escritorExcepcion = escritorExcepcion;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, error) ->
                                escritorExcepcion.escribir(response, "ERC-053"))
                        .accessDeniedHandler((request, response, error) ->
                                escritorExcepcion.escribir(response, "ERC-054")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/registro").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}