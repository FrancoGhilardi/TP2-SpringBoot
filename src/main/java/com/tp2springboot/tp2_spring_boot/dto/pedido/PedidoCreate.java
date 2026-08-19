package com.tp2springboot.tp2_spring_boot.dto.pedido;

import com.tp2springboot.tp2_spring_boot.dto.detallePedido.DetallePedidoCreate;
import com.tp2springboot.tp2_spring_boot.model.enums.FormaPago;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Id del usuario dueño del pedido", example = "1") @NotNull
        Long usuarioId,
    @Schema(description = "Medio de pago", example = "TARJETA") @NotNull FormaPago formaPago,
    @Schema(description = "Líneas del pedido, al menos una") @NotEmpty @Valid
        List<DetallePedidoCreate> detalles) {}
