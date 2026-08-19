package com.tp2springboot.tp2_spring_boot.dto.pedido;

import com.tp2springboot.tp2_spring_boot.dto.detallePedido.DetallePedidoDto;
import com.tp2springboot.tp2_spring_boot.model.enums.EstadoPedido;
import com.tp2springboot.tp2_spring_boot.model.enums.FormaPago;
import java.time.LocalDate;
import java.util.List;

/**
 * Representación de un pedido hacia el cliente. Es un DTO exclusivamente de salida; el alta se
 * hace con {@link PedidoCreate}.
 *
 * @param id identificador
 * @param fecha fecha de creación
 * @param estado estado del pedido
 * @param total total del pedido
 * @param formaPago medio de pago
 * @param usuarioId id del usuario dueño del pedido
 * @param usuarioMail mail del usuario (derivado)
 * @param detalles líneas del pedido
 */
public record PedidoDto(
    Long id,
    LocalDate fecha,
    EstadoPedido estado,
    Double total,
    FormaPago formaPago,
    Long usuarioId,
    String usuarioMail,
    List<DetallePedidoDto> detalles) {}
