package com.athenea.ms_usuarios.service;

import com.athenea.ms_usuarios.dto.request.UsuarioActualizacionDto;
import com.athenea.ms_usuarios.dto.request.UsuarioAdminActualizacionDto;
import com.athenea.ms_usuarios.dto.request.UsuarioRegistroDto;
import com.athenea.ms_usuarios.dto.response.UsuarioResponseDto;

import java.util.List;

public interface UsuarioService {

    UsuarioResponseDto registrar(UsuarioRegistroDto dto);

    List<UsuarioResponseDto> listar();

    UsuarioResponseDto buscarPorId(Long id);

    UsuarioResponseDto perfil(String correoAutenticado);

    UsuarioResponseDto actualizarPropio(String correoAutenticado, UsuarioActualizacionDto dto);

    UsuarioResponseDto actualizarPorAdmin(Long id, UsuarioAdminActualizacionDto dto);

    void eliminar(Long id, String correoAutenticado);
}
