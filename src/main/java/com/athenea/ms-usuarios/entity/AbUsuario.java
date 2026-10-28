package com.athenea.ms_usuarios.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    private Boolean correoVerificado = false;

    @Column(nullable = false)
    private Boolean activo = true;

    // Relación Many-to-One con Politica de Privacidad
    @ManyToOne(fetch = FetchType.LAZY) // Carga perezosa para mejorar el rendimiento
    @JoinColumn(name = "politica_id", nullable = false)
    private AbPoliticaPrivacidad politicaPrivacidad;

    // Relacion Many-to-Many con Roles a traves de la tabla intermedia ab_usuario_roles
    @ManyToMany(fetch = FetchType.EAGER) // Carga ansiosa para obtener los roles junto con el usuario
    @JoinTable(
        name = "ab_usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<AbRol> roles = new HashSet<>();

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Actualiza las fechas de registro y actualización antes de persistir o actualizar la entidad
    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.correoVerificado == null) this.correoVerificado = false;
        if (this.activo == null) this.activo = true;
    }

    // Actualiza la fecha de actualización antes de actualizar la entidad
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}