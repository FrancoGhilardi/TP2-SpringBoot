package com.tp2springboot.tp2_spring_boot.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Categoría a la que pertenecen uno o más {@link Producto}.
 */
@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(
    callSuper = true,
    exclude = {"productos"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Categoria extends Base {

  /** Nombre único de la categoría. */
  @EqualsAndHashCode.Include
  @Column(name = "nombre", nullable = false, unique = true, length = 100)
  private String nombre;

  /** Descripción libre de la categoría. */
  @Column(name = "descripcion", length = 500)
  private String descripcion;

  /** Productos que pertenecen a esta categoría (lado inverso de la relación). */
  @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Producto> productos = new HashSet<>();

  /**
   * Agrega un producto a la categoría y sincroniza el lado dueño de la relación.
   *
   * @param producto producto a asociar
   * @throws IllegalArgumentException si el producto ya estaba cargado en la categoría
   */
  public void addProducto(Producto producto) {
    if (!productos.add(producto)) {
      throw new IllegalArgumentException("Producto ya cargado en la categoría");
    }
    producto.setCategoria(this);
  }
}
