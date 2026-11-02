package com.athenea.ms_usuarios.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ab_politicas_privacidad")
public class AbPoliticaPrivacidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String version;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(name = "archivo_url", nullable = false, length = 255)
    private String archivoUrl;

    @Column(nullable = false)
    private Boolean vigente;

    @Column(name = "fecha_registro", updatable = false)
    private OffsetDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private OffsetDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        OffsetDateTime ahora = OffsetDateTime.now();
        this.fechaRegistro = ahora;
        this.fechaActualizacion = ahora;
        if (this.vigente == null) {
            this.vigente = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = OffsetDateTime.now();
    }
}