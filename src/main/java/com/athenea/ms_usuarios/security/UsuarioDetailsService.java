package com.athenea.ms_usuarios.security;

import com.athenea.ms_usuarios.repository.UsuarioRepository;
import com.athenea.ms_usuarios.util.Correos;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) {
        String normalizado = Correos.normalizar(correo);
        return usuarioRepository.findByCorreo(normalizado == null ? "" : normalizado)
                .map(UsuarioDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no disponible"));
    }
}
