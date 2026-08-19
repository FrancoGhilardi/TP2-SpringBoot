package com.tp2springboot.tp2_spring_boot.mapper;

import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaCreate;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaDto;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaEdit;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaPatch;
import com.tp2springboot.tp2_spring_boot.model.Categoria;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Conversión entre la entidad {@link Categoria} y sus DTOs.
 */
@Component
public class CategoriaMapper {

  /**
   * Construye una nueva entidad {@link Categoria} a partir de los datos de alta.
   */
  public Categoria toEntity(CategoriaCreate dto) {
    return Categoria.builder().nombre(dto.nombre()).descripcion(dto.descripcion()).build();
  }

  /**
   * Aplica los cambios de edición sobre una entidad existente.
   */
  public void updateEntity(Categoria entity, CategoriaEdit dto) {
    entity.setNombre(dto.nombre());
    entity.setDescripcion(dto.descripcion());
  }

  /**
   * Aplica sobre una entidad existente solo los campos no nulos del patch.
   */
  public void patchEntity(Categoria entity, CategoriaPatch dto) {
    if (dto.nombre() != null) {
      entity.setNombre(dto.nombre());
    }
    if (dto.descripcion() != null) {
      entity.setDescripcion(dto.descripcion());
    }
  }

  /**
   * Convierte la entidad a su DTO de salida, calculando {@code cantidadProductos} (solo
   * productos no eliminados).
   */
  public CategoriaDto toDto(Categoria entity) {
    long cantidadProductos =
        entity.getProductos() == null
            ? 0
            : entity.getProductos().stream().filter(p -> !p.isEliminado()).count();
    return new CategoriaDto(
        entity.getId(), entity.getNombre(), entity.getDescripcion(), (int) cantidadProductos);
  }

  /**
   * Convierte una lista de entidades a sus DTOs de salida.
   */
  public List<CategoriaDto> toDtoList(List<Categoria> entities) {
    return entities.stream().map(this::toDto).toList();
  }
}
