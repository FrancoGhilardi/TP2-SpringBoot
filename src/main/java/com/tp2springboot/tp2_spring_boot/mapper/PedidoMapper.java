package com.tp2springboot.tp2_spring_boot.mapper;

import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoDto;
import com.tp2springboot.tp2_spring_boot.model.Pedido;
import com.tp2springboot.tp2_spring_boot.model.Usuario;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Conversión entre la entidad {@link Pedido} y su DTO de salida. Delega el mapeo de cada línea
 * en {@link DetallePedidoMapper}, inyectado por constructor.
 */
@Component
public class PedidoMapper {

  private final DetallePedidoMapper detallePedidoMapper;

  public PedidoMapper(DetallePedidoMapper detallePedidoMapper) {
    this.detallePedidoMapper = detallePedidoMapper;
  }

  /**
   * Convierte la entidad a su DTO de salida, incluyendo los datos del usuario y de cada
   * detalle.
   */
  public PedidoDto toDto(Pedido entity) {
    Usuario usuario = entity.getUsuario();
    return new PedidoDto(
        entity.getId(),
        entity.getFecha(),
        entity.getEstado(),
        entity.getTotal(),
        entity.getFormaPago(),
        usuario != null ? usuario.getId() : null,
        usuario != null ? usuario.getMail() : null,
        entity.getDetalles().stream().map(detallePedidoMapper::toDto).toList());
  }

  /**
   * Convierte una lista de entidades a sus DTOs de salida.
   */
  public List<PedidoDto> toDtoList(List<Pedido> entities) {
    return entities.stream().map(this::toDto).toList();
  }
}
