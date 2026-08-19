package com.tp2springboot.tp2_spring_boot.mapper;

import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoCreate;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoDto;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoEdit;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoPatch;
import com.tp2springboot.tp2_spring_boot.model.Categoria;
import com.tp2springboot.tp2_spring_boot.model.Producto;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Conversión entre la entidad {@link Producto} y sus DTOs. No accede a repositorios: la
 * {@link Categoria} ya resuelta se recibe como parámetro.
 */
@Component
public class ProductoMapper {

  /**
   * Construye una nueva entidad {@link Producto} a partir de los datos de alta y la categoría
   * ya resuelta.
   */
  public Producto toEntity(ProductoCreate dto, Categoria categoria) {
    return Producto.builder()
        .nombre(dto.nombre())
        .precio(dto.precio())
        .descripcion(dto.descripcion())
        .stock(dto.stock())
        .imagen(dto.imagen())
        .disponible(dto.disponible() != null ? dto.disponible() : Boolean.TRUE)
        .categoria(categoria)
        .build();
  }

  /**
   * Aplica los cambios de edición sobre una entidad existente, incluida la nueva categoría.
   */
  public void updateEntity(Producto entity, ProductoEdit dto, Categoria categoria) {
    entity.setNombre(dto.nombre());
    entity.setPrecio(dto.precio());
    entity.setDescripcion(dto.descripcion());
    entity.setStock(dto.stock());
    entity.setImagen(dto.imagen());
    entity.setDisponible(dto.disponible() != null ? dto.disponible() : Boolean.TRUE);
    entity.setCategoria(categoria);
  }

  /**
   * Aplica sobre una entidad existente solo los campos no nulos del patch. La categoría llega
   * ya resuelta (o {@code null} si el patch no la trae).
   */
  public void patchEntity(Producto entity, ProductoPatch dto, Categoria categoria) {
    if (dto.nombre() != null) {
      entity.setNombre(dto.nombre());
    }
    if (dto.precio() != null) {
      entity.setPrecio(dto.precio());
    }
    if (dto.descripcion() != null) {
      entity.setDescripcion(dto.descripcion());
    }
    if (dto.stock() != null) {
      entity.setStock(dto.stock());
    }
    if (dto.imagen() != null) {
      entity.setImagen(dto.imagen());
    }
    if (dto.disponible() != null) {
      entity.setDisponible(dto.disponible());
    }
    if (categoria != null) {
      entity.setCategoria(categoria);
    }
  }

  /**
   * Convierte la entidad a su DTO de salida, incluyendo id y nombre de la categoría.
   */
  public ProductoDto toDto(Producto entity) {
    Categoria categoria = entity.getCategoria();
    return new ProductoDto(
        entity.getId(),
        entity.getNombre(),
        entity.getPrecio(),
        entity.getDescripcion(),
        entity.getStock(),
        entity.getImagen(),
        entity.getDisponible(),
        categoria != null ? categoria.getId() : null,
        categoria != null ? categoria.getNombre() : null);
  }

  /**
   * Convierte una lista de entidades a sus DTOs de salida.
   */
  public List<ProductoDto> toDtoList(List<Producto> entities) {
    return entities.stream().map(this::toDto).toList();
  }
}
