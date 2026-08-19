package com.tp2springboot.tp2_spring_boot.dto.producto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos de entrada para dar de alta un producto.
 *
 * @param nombre nombre del producto, obligatorio
 * @param precio precio unitario, obligatorio y positivo
 * @param descripcion descripción libre, opcional
 * @param stock cantidad inicial en stock, obligatoria y no negativa
 * @param imagen URL o path de la imagen, opcional
 * @param disponible si el producto está disponible para la venta
 * @param categoriaId id de la categoría a la que pertenece, obligatorio
 */
public record ProductoCreate(
    @Schema(description = "Nombre del producto", example = "Mouse óptico") @NotBlank
        String nombre,
    @Schema(description = "Precio unitario", example = "4999.90") @NotNull @Positive
        Double precio,
    @Schema(description = "Descripción libre del producto", example = "Mouse óptico inalámbrico")
        String descripcion,
    @Schema(description = "Cantidad inicial en stock", example = "50") @NotNull @PositiveOrZero
        Integer stock,
    @Schema(description = "URL o path de la imagen", example = "https://example.com/mouse.png")
        String imagen,
    @Schema(description = "Si el producto está disponible para la venta", example = "true")
        Boolean disponible,
    @Schema(description = "Id de la categoría a la que pertenece", example = "1") @NotNull
        Long categoriaId) {}
