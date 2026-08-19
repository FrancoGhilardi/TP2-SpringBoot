package com.tp2springboot.tp2_spring_boot.dto.categoria;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos de entrada para editar una categoría existente. El id viaja por el path.
 *
 * @param nombre nuevo nombre, obligatorio
 * @param descripcion nueva descripción, opcional
 */
public record CategoriaEdit(
    @Schema(description = "Nuevo nombre de la categoría", example = "Electrónica") @NotBlank
        String nombre,
    @Schema(description = "Nueva descripción", example = "Gadgets, dispositivos y accesorios")
        String descripcion) {}
