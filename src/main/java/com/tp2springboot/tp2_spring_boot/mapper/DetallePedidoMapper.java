package com.tp2springboot.tp2_spring_boot.mapper;

import com.tp2springboot.tp2_spring_boot.dto.detallePedido.DetallePedidoDto;
import com.tp2springboot.tp2_spring_boot.model.DetallePedido;
import org.springframework.stereotype.Component;

/**
 * Conversión entre la entidad {@link DetallePedido} y su DTO de salida.
 */
@Component
public class DetallePedidoMapper {

  /**
   * Convierte la entidad a su DTO de salida, incluyendo datos derivados del producto.
   */
  public DetallePedidoDto toDto(DetallePedido entity) {
    return new DetallePedidoDto(
        entity.getId(),
        entity.getProducto().getId(),
        entity.getProducto().getNombre(),
        entity.getProducto().getPrecio(),
        entity.getCantidad(),
        entity.getSubtotal());
  }
}
