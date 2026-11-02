package com.athenea.ms_usuarios.repository;

import com.athenea.ms_usuarios.entity.AbUsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRolRepository extends JpaRepository<AbUsuarioRol, AbUsuarioRol.UsuarioRolId> {

    java.util.List<AbUsuarioRol> findByUsuario_Id(Long usuarioId);

}