package com.tp2springboot.tp2_spring_boot.dto.categoria;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos de entrada para dar de alta una categoría.
 *
 * @param nombre nombre de la categoría, obligatorio
 * @param descripcion descripción libre, opcional
 */
public record CategoriaCreate(@NotBlank String nombre, String descripcion) {}
