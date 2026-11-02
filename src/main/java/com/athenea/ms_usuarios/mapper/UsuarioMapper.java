package com.athenea.ms_usuarios.mapper;

import com.athenea.ms_usuarios.dto.response.UsuarioResponse;
import com.athenea.ms_usuarios.entity.AbUsuario;
import com.athenea.ms_usuarios.exception.ExcepcionNegocio;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse aRespuesta(AbUsuario usuario) {
        if (usuario.getPolitica() == null) {
            throw new ExcepcionNegocio("ERC-070");
        }
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getNombreCompleto(),
                usuario.getFechaNacimiento(),
                usuario.getCorreoVerificado(),
                usuario.getActivo(),
                usuario.getPolitica().getId());
    }
}