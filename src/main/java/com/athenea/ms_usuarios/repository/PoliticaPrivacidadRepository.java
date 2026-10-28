package com.athenea.ms_usuarios.repository;

import com.athenea.ms_usuarios.entity.AbPoliticaPrivacidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoliticaPrivacidadRepository extends JpaRepository<AbPoliticaPrivacidad, Long> {
}
