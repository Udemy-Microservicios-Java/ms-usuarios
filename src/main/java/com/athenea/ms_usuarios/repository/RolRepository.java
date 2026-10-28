package com.athenea.ms_usuarios.repository;

import com.athenea.ms_usuarios.entity.AbRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<AbRol, Long> {

    Optional<AbRol> findByNombre(String nombre);
}
