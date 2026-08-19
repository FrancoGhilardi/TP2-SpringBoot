package com.tp2springboot.tp2_spring_boot.exception;

/**
 * Excepción de dominio lanzada cuando se busca una entidad que no existe o está dada de baja.
 * Se traduce a {@code 404 NOT_FOUND} por {@link GlobalExceptionHandler}.
 */
public class RecursoNoEncontradoException extends RuntimeException {

  /**
   * @param mensaje detalle del recurso no encontrado
   */
  public RecursoNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
