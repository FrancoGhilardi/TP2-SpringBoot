package com.tp2springboot.tp2_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Producto vendible, asociado a una {@link Categoria}.
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"categoria"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Producto extends Base {

  /** Nombre del producto. */
  @EqualsAndHashCode.Include
  @Column(name = "nombre", nullable = false, length = 100)
  private String nombre;

  /** Precio unitario. */
  @Column(name = "precio", nullable = false)
  private Double precio;

  /** Descripción libre del producto. */
  @Column(name = "descripcion", length = 500)
  private String descripcion;

  /** Cantidad disponible en stock. */
  @Column(name = "stock", nullable = false)
  private Integer stock;

  /** URL o path de la imagen del producto. */
  @Column(name = "imagen")
  private String imagen;

  /** Indica si el producto está disponible para la venta. */
  @Builder.Default
  @Column(name = "disponible")
  private Boolean disponible = Boolean.TRUE;

  /** Categoría a la que pertenece el producto (lado dueño de la relación). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id")
  private Categoria categoria;
}
