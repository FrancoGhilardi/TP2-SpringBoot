package com.tp2springboot.tp2_spring_boot.dto.categoria;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos de entrada para dar de alta una categoría.
 *
 * @param nombre nombre de la categoría, obligatorio
 * @param descripcion descripción libre, opcional
 */
public record CategoriaCreate(
    @Schema(description = "Nombre de la categoría", example = "Electrónica") @NotBlank
        String nombre,
    @Schema(description = "Descripción libre de la categoría", example = "Gadgets y dispositivos")
        String descripcion) {}
