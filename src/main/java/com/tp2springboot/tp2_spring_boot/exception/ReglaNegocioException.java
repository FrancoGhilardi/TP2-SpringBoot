package com.tp2springboot.tp2_spring_boot.exception;

/**
 * Excepción de dominio lanzada cuando se viola una regla de negocio (stock insuficiente, mail
 * duplicado, transición de estado inválida, etc.). Se traduce a {@code 409 CONFLICT} por
 * {@link GlobalExceptionHandler}.
 */
public class ReglaNegocioException extends RuntimeException {

  /**
   * @param mensaje detalle de la regla de negocio violada
   */
  public ReglaNegocioException(String mensaje) {
    super(mensaje);
  }
}
