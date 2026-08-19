package com.tp2springboot.tp2_spring_boot.model;

import com.tp2springboot.tp2_spring_boot.model.enums.EstadoPedido;
import com.tp2springboot.tp2_spring_boot.model.enums.FormaPago;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Pedido realizado por un {@link Usuario}, compuesto por uno o más {@link DetallePedido}.
 */
@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(
    callSuper = true,
    exclude = {"detalles", "usuario"})
@EqualsAndHashCode(callSuper = true)
public class Pedido extends Base implements Calculable {

  /** Fecha de creación del pedido, no modificable luego del alta. */
  @Column(name = "fecha", updatable = false)
  @Builder.Default
  private LocalDate fecha = LocalDate.now();

  /** Estado actual del pedido dentro de su ciclo de vida. */
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false, length = 30)
  @Builder.Default
  private EstadoPedido estado = EstadoPedido.PENDIENTE;

  /** Total del pedido, suma de los subtotales de sus detalles. */
  @Column(name = "total", nullable = false)
  @Builder.Default
  private Double total = 0.0;

  /** Medio de pago elegido para el pedido. */
  @Enumerated(EnumType.STRING)
  @Column(name = "forma_pago", nullable = false, length = 20)
  private FormaPago formaPago;

  /** Usuario dueño del pedido (lado dueño de la relación). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  /** Detalles (líneas) que componen el pedido. */
  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<DetallePedido> detalles = new HashSet<>();

  /**
   * Crea un {@link DetallePedido} para el producto y cantidad indicados, lo agrega al pedido y
   * acumula su subtotal al total.
   *
   * @param cantidad cantidad de unidades del producto
   * @param producto producto a agregar
   */
  public void addDetallePedido(int cantidad, Producto producto) {
    DetallePedido detalle =
        DetallePedido.builder()
            .cantidad(cantidad)
            .producto(producto)
            .subtotal(producto.getPrecio() * cantidad)
            .build();

    detalle.setPedido(this);
    this.detalles.add(detalle);
    this.total += detalle.getSubtotal();
  }

  /**
   * Recalcula {@link #total} como la suma de los subtotales de todos los detalles no nulos.
   */
  @Override
  public void calcularTotal() {
    double acumulador = 0.0;
    for (DetallePedido detalle : detalles) {
      if (detalle.getSubtotal() != null) {
        acumulador += detalle.getSubtotal();
      }
    }
    this.total = acumulador;
  }

  /**
   * Busca el detalle del pedido correspondiente a un producto.
   *
   * @param producto producto a buscar
   * @return el detalle encontrado, o {@code null} si el pedido no tiene un detalle con ese producto
   */
  public DetallePedido findDetallePedidoByProducto(Producto producto) {
    for (DetallePedido detalle : detalles) {
      if (detalle.getProducto() != null && detalle.getProducto().getId().equals(producto.getId())) {
        return detalle;
      }
    }
    return null;
  }

  /**
   * Elimina el detalle del pedido asociado a un producto (si existe) y recalcula el total.
   *
   * @param producto producto cuyo detalle se quiere eliminar
   */
  public void deleteDetallePedidoByProducto(Producto producto) {
    DetallePedido detalleEncontrado = findDetallePedidoByProducto(producto);
    if (detalleEncontrado != null) {
      detalles.remove(detalleEncontrado);
      calcularTotal();
    }
  }
}
