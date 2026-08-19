package com.tp2springboot.tp2_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Línea de un {@link Pedido}: cantidad de un {@link Producto} y su subtotal.
 */
@Entity
@Table(name = "detalles_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"pedido"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class DetallePedido extends Base {

  /** Cantidad de unidades del producto. */
  @Column(name = "cantidad", nullable = false)
  private Integer cantidad;

  /** Subtotal de la línea (precio del producto × cantidad). */
  @Column(name = "subtotal", nullable = false)
  private Double subtotal;

  /** Producto de esta línea. */
  @EqualsAndHashCode.Include
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "producto_id", nullable = false)
  private Producto producto;

  /** Pedido al que pertenece esta línea (lado dueño de la relación). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pedido_id")
  private Pedido pedido;

  /**
   * Recalcula {@link #subtotal} como precio del producto × cantidad.
   */
  public void calcularSubtotal() {
    if (this.producto != null && this.cantidad != null) {
      this.subtotal = this.producto.getPrecio() * this.cantidad;
    }
  }
}
