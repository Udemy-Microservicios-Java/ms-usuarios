package com.athenea.ms_usuarios.repository;

import com.athenea.ms_usuarios.entity.AbUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<AbUsuario, Long> {

    Optional<AbUsuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByNombreUsuario(String nombreUsuario);
}
