package com.tp2springboot.tp2_spring_boot.dto.pedido;

import com.tp2springboot.tp2_spring_boot.model.enums.EstadoPedido;
import com.tp2springboot.tp2_spring_boot.model.enums.FormaPago;

/**
 * Datos de entrada para editar el estado y/o la forma de pago de un pedido existente. El id
 * viaja por el path.
 *
 * @param estado nuevo estado, opcional (si es {@code null} no se modifica)
 * @param formaPago nueva forma de pago, opcional (si es {@code null} no se modifica)
 */
public record PedidoEdit(EstadoPedido estado, FormaPago formaPago) {}
