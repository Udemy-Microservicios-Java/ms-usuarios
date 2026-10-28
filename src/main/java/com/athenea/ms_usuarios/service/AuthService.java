package com.athenea.ms_usuarios.service;

import com.athenea.ms_usuarios.dto.request.LoginRequestDto;
import com.athenea.ms_usuarios.dto.response.LoginResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto dto);
}
