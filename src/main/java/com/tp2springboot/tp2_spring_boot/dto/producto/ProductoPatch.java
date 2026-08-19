package com.tp2springboot.tp2_spring_boot.dto.producto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos de entrada para editar parcialmente un producto existente. Todos los campos son
 * opcionales; solo se aplican los que llegan no nulos.
 *
 * @param nombre nuevo nombre, opcional
 * @param precio nuevo precio unitario, opcional y positivo si se envía
 * @param descripcion nueva descripción, opcional
 * @param stock nuevo stock, opcional y no negativo si se envía
 * @param imagen nueva imagen, opcional
 * @param disponible nueva disponibilidad, opcional
 * @param categoriaId id de la nueva categoría, opcional
 */
public record ProductoPatch(
    @Schema(description = "Nuevo nombre del producto", example = "Mouse óptico")
        String nombre,
    @Schema(description = "Nuevo precio unitario", example = "5499.90") @Positive
        Double precio,
    @Schema(description = "Nueva descripción", example = "Mouse óptico inalámbrico")
        String descripcion,
    @Schema(description = "Nuevo stock", example = "40") @PositiveOrZero Integer stock,
    @Schema(description = "Nueva imagen", example = "https://example.com/mouse.png")
        String imagen,
    @Schema(description = "Nueva disponibilidad", example = "true") Boolean disponible,
    @Schema(description = "Id de la nueva categoría", example = "1") Long categoriaId) {}
