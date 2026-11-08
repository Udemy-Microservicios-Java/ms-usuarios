package com.athenea.ms_usuarios.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "ab_usuarios")
public class AbUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(name = "nombre_completo", length = 120)
    private String nombreCompleto;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "correo_verificado", nullable = false)
    private Boolean correoVerificado;

    @Column(nullable = false)
    private Boolean activo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "politica_id", nullable = false)
    private AbPoliticaPrivacidad politica;

    @Column(name = "fecha_registro", updatable = false)
    private OffsetDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private OffsetDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        OffsetDateTime ahora = OffsetDateTime.now();
        this.fechaRegistro = ahora;
        this.fechaActualizacion = ahora;
        if (this.correoVerificado == null) {
            this.correoVerificado = false;
        }
        if (this.activo == null) {
            this.activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = OffsetDateTime.now();
    }
}