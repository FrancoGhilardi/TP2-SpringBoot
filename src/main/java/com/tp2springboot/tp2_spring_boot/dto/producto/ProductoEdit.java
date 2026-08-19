package com.tp2springboot.tp2_spring_boot.dto.producto;

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
    @NotBlank String nombre,
    @NotNull @Positive Double precio,
    String descripcion,
    @NotNull @PositiveOrZero Integer stock,
    String imagen,
    Boolean disponible,
    @NotNull Long categoriaId) {}
