package com.tp2springboot.tp2_spring_boot.model;

/**
 * Contrato para entidades que pueden recalcular un total propio a partir de sus componentes.
 */
public interface Calculable {

  /**
   * Recalcula y actualiza el total de la entidad.
   */
  void calcularTotal();
}
