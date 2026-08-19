package com.tp2springboot.tp2_spring_boot.dto.categoria;

/**
 * Representación de una categoría hacia el cliente.
 *
 * @param id identificador
 * @param nombre nombre de la categoría
 * @param descripcion descripción libre
 * @param cantidadProductos cantidad de productos asociados (derivado)
 */
public record CategoriaDto(Long id, String nombre, String descripcion, int cantidadProductos) {}
