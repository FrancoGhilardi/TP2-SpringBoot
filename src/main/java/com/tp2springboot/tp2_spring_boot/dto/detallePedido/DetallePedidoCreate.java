package com.tp2springboot.tp2_spring_boot.dto.detallePedido;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Datos de entrada para agregar una línea de detalle a un pedido.
 *
 * @param productoId id del producto a agregar, obligatorio
 * @param cantidad cantidad de unidades, obligatoria y positiva
 */
public record DetallePedidoCreate(@NotNull Long productoId, @NotNull @Positive Integer cantidad) {}
