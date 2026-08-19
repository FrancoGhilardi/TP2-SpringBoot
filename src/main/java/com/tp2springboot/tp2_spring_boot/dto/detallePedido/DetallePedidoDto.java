package com.tp2springboot.tp2_spring_boot.dto.detallePedido;

/**
 * Representación de una línea de detalle de pedido hacia el cliente.
 *
 * @param id identificador
 * @param productoId id del producto de la línea
 * @param productoNombre nombre del producto (derivado)
 * @param precioUnitario precio unitario del producto al momento de la consulta (derivado)
 * @param cantidad cantidad de unidades
 * @param subtotal subtotal de la línea (precio × cantidad)
 */
public record DetallePedidoDto(
    Long id,
    Long productoId,
    String productoNombre,
    Double precioUnitario,
    Integer cantidad,
    Double subtotal) {}
