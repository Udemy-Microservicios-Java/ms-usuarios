package com.athenea.ms_usuarios.repository;

import com.athenea.ms_usuarios.entity.AbUsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRolRepository extends JpaRepository<AbUsuarioRol, AbUsuarioRol.UsuarioRolId> {

    @Query("""
            select rol.nombre
            from AbUsuarioRol relacion
            join relacion.rol rol
            where relacion.usuario.id = :usuarioId
            """)
    List<String> findNombresByUsuarioId(@Param("usuarioId") Long usuarioId);
}