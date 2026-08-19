package com.tp2springboot.tp2_spring_boot.dto.producto;

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
    @NotBlank String nombre,
    @NotNull @Positive Double precio,
    String descripcion,
    @NotNull @PositiveOrZero Integer stock,
    String imagen,
    Boolean disponible,
    @NotNull Long categoriaId) {}
