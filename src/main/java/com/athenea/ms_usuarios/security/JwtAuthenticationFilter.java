package com.athenea.ms_usuarios.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims datos = jwtService.leer(token);
                UsernamePasswordAuthenticationToken autenticado =
                        new UsernamePasswordAuthenticationToken(
                                datos.getSubject(), null, autoridades(datos));
                SecurityContextHolder.getContext().setAuthentication(autenticado);
            } catch (JwtException error) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    private List<GrantedAuthority> autoridades(Claims datos) {
        List<?> roles = datos.get("roles", List.class);
        List<GrantedAuthority> autoridades = new ArrayList<>();
        if (roles != null) {
            for (Object rol : roles) {
                autoridades.add(new SimpleGrantedAuthority(String.valueOf(rol)));
            }
        }
        return autoridades;
    }
}