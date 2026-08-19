package com.tp2springboot.tp2_spring_boot.dto.pedido;

import com.tp2springboot.tp2_spring_boot.model.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;

/**
 * Datos de entrada para cambiar el estado de un pedido existente.
 *
 * @param estado nuevo estado, obligatorio
 */
public record PedidoEstadoPatch(@NotNull EstadoPedido estado) {}
