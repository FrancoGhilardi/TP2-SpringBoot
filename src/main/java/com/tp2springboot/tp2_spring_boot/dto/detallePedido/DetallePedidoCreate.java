package com.tp2springboot.tp2_spring_boot.dto.detallePedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Datos de entrada para agregar una línea de detalle a un pedido.
 *
 * @param productoId id del producto a agregar, obligatorio
 * @param cantidad cantidad de unidades, obligatoria y positiva
 */
public record DetallePedidoCreate(
    @Schema(description = "Id del producto a agregar", example = "1") @NotNull Long productoId,
    @Schema(description = "Cantidad de unidades", example = "2") @NotNull @Positive
        Integer cantidad) {}
