package com.tp2springboot.tp2_spring_boot.dto.categoria;

/**
 * Datos de entrada para editar parcialmente una categoría existente. Todos los campos son
 * opcionales; solo se aplican los que llegan no nulos.
 *
 * @param nombre nuevo nombre, opcional
 * @param descripcion nueva descripción, opcional
 */
public record CategoriaPatch(String nombre, String descripcion) {}
