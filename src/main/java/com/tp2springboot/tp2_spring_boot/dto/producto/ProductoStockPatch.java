package com.tp2springboot.tp2_spring_boot.dto.producto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos de entrada para actualizar el stock de un producto.
 *
 * @param stock nuevo stock, obligatorio y no negativo
 */
public record ProductoStockPatch(@NotNull @PositiveOrZero Integer stock) {}
