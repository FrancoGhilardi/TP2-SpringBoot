package com.tp2springboot.tp2_spring_boot.dto.producto;

/**
 * Representación de un producto hacia el cliente.
 *
 * @param id identificador
 * @param nombre nombre del producto
 * @param precio precio unitario
 * @param descripcion descripción libre
 * @param stock cantidad disponible en stock
 * @param imagen URL o path de la imagen
 * @param disponible si el producto está disponible para la venta
 * @param categoriaId id de la categoría a la que pertenece
 * @param categoriaNombre nombre de la categoría (derivado)
 */
public record ProductoDto(
    Long id,
    String nombre,
    Double precio,
    String descripcion,
    Integer stock,
    String imagen,
    Boolean disponible,
    Long categoriaId,
    String categoriaNombre) {}
