package com.tp2springboot.tp2_spring_boot.dto.categoria;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Datos de entrada para editar parcialmente una categoría existente. Todos los campos son
 * opcionales; solo se aplican los que llegan no nulos.
 *
 * @param nombre nuevo nombre, opcional
 * @param descripcion nueva descripción, opcional
 */
public record CategoriaPatch(
    @Schema(description = "Nuevo nombre de la categoría", example = "Electrónica")
        String nombre,
    @Schema(description = "Nueva descripción", example = "Gadgets, dispositivos y accesorios")
        String descripcion) {}
