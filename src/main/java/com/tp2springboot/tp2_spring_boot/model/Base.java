package com.tp2springboot.tp2_spring_boot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Superclase mapeada con los campos comunes a todas las entidades: id autogenerado, marca de
 * baja lógica y fecha de creación.
 */
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class Base {

  /** Identificador autogenerado por la base de datos. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Marca de baja lógica: {@code true} si el registro fue eliminado. */
  @Column(name = "eliminado", nullable = false)
  @Builder.Default
  private boolean eliminado = false;

  /** Fecha y hora de creación del registro, no modificable luego del alta. */
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  /**
   * Setea {@link #createdAt} con la fecha/hora actual si todavía no fue asignada, justo antes de
   * persistir la entidad por primera vez.
   */
  @PrePersist
  protected void prePersist() {
    if (this.createdAt == null) {
      this.createdAt = LocalDateTime.now();
    }
  }
}
