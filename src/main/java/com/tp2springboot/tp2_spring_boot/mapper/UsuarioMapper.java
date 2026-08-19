package com.tp2springboot.tp2_spring_boot.mapper;

import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioCreate;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioDto;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioEdit;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioPatch;
import com.tp2springboot.tp2_spring_boot.model.Usuario;
import com.tp2springboot.tp2_spring_boot.model.enums.Rol;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Conversión entre la entidad {@link Usuario} y sus DTOs. Nunca expone la contraseña hacia el
 * cliente.
 */
@Component
public class UsuarioMapper {

  /**
   * Construye una nueva entidad {@link Usuario} a partir de los datos de alta.
   */
  public Usuario toEntity(UsuarioCreate dto) {
    return Usuario.builder()
        .nombre(dto.nombre())
        .apellido(dto.apellido())
        .mail(dto.mail())
        .celular(dto.celular())
        .contrasena(dto.contrasena())
        .rol(dto.rol() != null ? dto.rol() : Rol.USUARIO)
        .build();
  }

  /**
   * Aplica los cambios de edición sobre una entidad existente. No modifica la contraseña.
   */
  public void updateEntity(Usuario entity, UsuarioEdit dto) {
    entity.setNombre(dto.nombre());
    entity.setApellido(dto.apellido());
    entity.setMail(dto.mail());
    entity.setCelular(dto.celular());
    entity.setRol(dto.rol());
  }

  /**
   * Aplica sobre una entidad existente solo los campos no nulos del patch. No modifica la
   * contraseña.
   */
  public void patchEntity(Usuario entity, UsuarioPatch dto) {
    if (dto.nombre() != null) {
      entity.setNombre(dto.nombre());
    }
    if (dto.apellido() != null) {
      entity.setApellido(dto.apellido());
    }
    if (dto.mail() != null) {
      entity.setMail(dto.mail());
    }
    if (dto.celular() != null) {
      entity.setCelular(dto.celular());
    }
    if (dto.rol() != null) {
      entity.setRol(dto.rol());
    }
  }

  /**
   * Convierte la entidad a su DTO de salida, sin incluir la contraseña.
   */
  public UsuarioDto toDto(Usuario entity) {
    int cantidadPedidos = entity.getPedidos() == null ? 0 : entity.getPedidos().size();
    return new UsuarioDto(
        entity.getId(),
        entity.getNombre(),
        entity.getApellido(),
        entity.getMail(),
        entity.getCelular(),
        entity.getRol(),
        cantidadPedidos);
  }

  /**
   * Convierte una lista de entidades a sus DTOs de salida.
   */
  public List<UsuarioDto> toDtoList(List<Usuario> entities) {
    return entities.stream().map(this::toDto).toList();
  }
}
