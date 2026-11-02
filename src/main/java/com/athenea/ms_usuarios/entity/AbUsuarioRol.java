package com.athenea.ms_usuarios.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ab_usuario_roles")
public class AbUsuarioRol {

    @EmbeddedId
    private UsuarioRolId id = new UsuarioRolId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private AbUsuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rolId")
    @JoinColumn(name = "rol_id")
    private AbRol rol;

    @Column(name = "fecha_registro", updatable = false)
    private OffsetDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = OffsetDateTime.now();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Embeddable
    public static class UsuarioRolId implements Serializable {

        @Column(name = "usuario_id")
        private Long usuarioId;

        @Column(name = "rol_id")
        private Long rolId;

        @Override
        public boolean equals(Object otro) {
            if (this == otro) {
                return true;
            }
            if (!(otro instanceof UsuarioRolId)) {
                return false;
            }
            UsuarioRolId ese = (UsuarioRolId) otro;
            return Objects.equals(usuarioId, ese.usuarioId) && Objects.equals(rolId, ese.rolId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(usuarioId, rolId);
        }
    }
}