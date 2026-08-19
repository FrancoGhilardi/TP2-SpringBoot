package com.tp2springboot.tp2_spring_boot.model;

import com.tp2springboot.tp2_spring_boot.model.enums.Rol;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Usuario del sistema, dueño de cero o más {@link Pedido}.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(
    callSuper = true,
    exclude = {"pedidos"})
public class Usuario extends Base {

  /** Nombre de pila. */
  @Column(name = "nombre", nullable = false, length = 50)
  private String nombre;

  /** Apellido. */
  @Column(name = "apellido", nullable = false, length = 50)
  private String apellido;

  /** Mail único, usado como identificador de negocio. */
  @EqualsAndHashCode.Include
  @Column(name = "mail", nullable = false, unique = true, length = 150)
  private String mail;

  /** Teléfono celular de contacto. */
  @Column(name = "celular", length = 20)
  private String celular;

  /** Contraseña del usuario. */
  @Column(name = "contrasena")
  private String contrasena;

  /** Rol del usuario dentro del sistema. */
  @Enumerated(EnumType.STRING)
  @Column(name = "rol", nullable = false, length = 20)
  @Builder.Default
  private Rol rol = Rol.USUARIO;

  /** Pedidos realizados por el usuario (lado inverso de la relación). */
  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Pedido> pedidos = new HashSet<>();

  /**
   * Asocia un pedido al usuario y sincroniza el lado dueño de la relación.
   *
   * @param pedido pedido a asociar
   */
  public void addPedido(Pedido pedido) {
    pedidos.add(pedido);
    pedido.setUsuario(this);
  }

  /**
   * Desasocia un pedido del usuario y sincroniza el lado dueño de la relación.
   *
   * @param pedido pedido a quitar
   */
  public void removePedido(Pedido pedido) {
    pedidos.remove(pedido);
    pedido.setUsuario(null);
  }
}
