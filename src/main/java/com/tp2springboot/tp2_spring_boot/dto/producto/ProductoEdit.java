package com.tp2springboot.tp2_spring_boot.dto.producto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos de entrada para editar un producto existente. El id viaja por el path.
 *
 * @param nombre nuevo nombre, obligatorio
 * @param precio nuevo precio unitario, obligatorio y positivo
 * @param descripcion nueva descripción, opcional
 * @param stock nuevo stock, obligatorio y no negativo
 * @param imagen nueva imagen, opcional
 * @param disponible nueva disponibilidad
 * @param categoriaId id de la nueva categoría, obligatorio
 */
public record ProductoEdit(
    @Schema(description = "Nuevo nombre del producto", example = "Mouse óptico") @NotBlank
        String nombre,
    @Schema(description = "Nuevo precio unitario", example = "5499.90") @NotNull @Positive
        Double precio,
    @Schema(description = "Nueva descripción", example = "Mouse óptico inalámbrico")
        String descripcion,
    @Schema(description = "Nuevo stock", example = "40") @NotNull @PositiveOrZero
        Integer stock,
    @Schema(description = "Nueva imagen", example = "https://example.com/mouse.png")
        String imagen,
    @Schema(description = "Nueva disponibilidad", example = "true") Boolean disponible,
    @Schema(description = "Id de la nueva categoría", example = "1") @NotNull
        Long categoriaId) {}
