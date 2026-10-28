package com.athenea.ms_usuarios.security;

import com.athenea.ms_usuarios.config.FiltroTraza;
import com.athenea.ms_usuarios.exception.CodigosExcepcion;
import com.athenea.ms_usuarios.service.ExcepcionCatalogoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class ManejadorSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ExcepcionCatalogoService catalogo;
    private final ObjectMapper objectMapper;

    public ManejadorSeguridad(ExcepcionCatalogoService catalogo, ObjectMapper objectMapper) {
        this.catalogo = catalogo;
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        escribir(request, response, CodigosExcepcion.NO_AUTENTICADO);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        escribir(request, response, CodigosExcepcion.SIN_PERMISO);
    }

    private void escribir(HttpServletRequest request, HttpServletResponse response, String codigo) throws IOException {
        ExcepcionCatalogoService.ErrorPreparada preparada = catalogo.preparar(codigo, traza(request));
        response.setStatus(preparada.http());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), preparada.cuerpo());
    }

    private String traza(HttpServletRequest request) {
        Object valor = request.getAttribute(FiltroTraza.ATRIBUTO);
        return valor == null ? UUID.randomUUID().toString() : valor.toString();
    }
}
