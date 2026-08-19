package com.tp2springboot.tp2_spring_boot.dto.pedido;

import com.tp2springboot.tp2_spring_boot.model.enums.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Datos de entrada para cambiar el estado de un pedido existente.
 *
 * @param estado nuevo estado, obligatorio
 */
public record PedidoEstadoPatch(
    @Schema(description = "Nuevo estado del pedido", example = "CONFIRMADO") @NotNull
        EstadoPedido estado) {}
