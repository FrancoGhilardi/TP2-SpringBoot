package com.tp2springboot.tp2_spring_boot.dto.pedido;

import com.tp2springboot.tp2_spring_boot.dto.detallePedido.DetallePedidoCreate;
import com.tp2springboot.tp2_spring_boot.model.enums.FormaPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Datos de entrada para dar de alta un pedido, con sus líneas de detalle.
 *
 * @param usuarioId id del usuario dueño del pedido, obligatorio
 * @param formaPago medio de pago, obligatorio
 * @param detalles líneas del pedido, obligatorias, al menos una
 */
public record PedidoCreate(
    @NotNull Long usuarioId,
    @NotNull FormaPago formaPago,
    @NotEmpty @Valid List<DetallePedidoCreate> detalles) {}
