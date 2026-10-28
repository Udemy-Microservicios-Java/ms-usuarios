package com.athenea.ms_usuarios.mapper;

import com.athenea.ms_usuarios.dto.response.UsuarioResponseDto;
import com.athenea.ms_usuarios.entity.AbUsuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioMapper {

    public UsuarioResponseDto aRespuesta(AbUsuario usuario) {
        UsuarioResponseDto respuesta = new UsuarioResponseDto();
        respuesta.setId(usuario.getId());
        respuesta.setNombreUsuario(usuario.getNombreUsuario());
        respuesta.setCorreo(usuario.getCorreo());
        respuesta.setNombreCompleto(usuario.getNombreCompleto());
        respuesta.setFechaNacimiento(usuario.getFechaNacimiento());
        respuesta.setCorreoVerificado(usuario.getCorreoVerificado());
        respuesta.setActivo(usuario.getActivo());
        respuesta.setFechaRegistro(usuario.getFechaRegistro());
        if (usuario.getPoliticaPrivacidad() != null) {
            respuesta.setPoliticaId(usuario.getPoliticaPrivacidad().getId());
        }
        if (usuario.getRoles() == null) {
            respuesta.setRoles(List.of());
        } else {
            respuesta.setRoles(usuario.getRoles().stream()
                    .map(rol -> rol.getNombre())
                    .sorted()
                    .toList());
        }
        return respuesta;
    }
}
