package com.tp2springboot.tp2_spring_boot.dto.categoria;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos de entrada para editar una categoría existente. El id viaja por el path.
 *
 * @param nombre nuevo nombre, obligatorio
 * @param descripcion nueva descripción, opcional
 */
public record CategoriaEdit(@NotBlank String nombre, String descripcion) {}
