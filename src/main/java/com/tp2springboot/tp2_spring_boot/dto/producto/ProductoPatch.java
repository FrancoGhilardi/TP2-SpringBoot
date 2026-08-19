package com.tp2springboot.tp2_spring_boot.dto.producto;

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
    String nombre,
    @Positive Double precio,
    String descripcion,
    @PositiveOrZero Integer stock,
    String imagen,
    Boolean disponible,
    Long categoriaId) {}
