package com.tp2springboot.tp2_spring_boot.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Manejador centralizado de excepciones: traduce las excepciones de dominio, de validación y de
 * infraestructura a respuestas HTTP consistentes ({@link ErrorResponse}).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Traduce {@link RecursoNoEncontradoException} a {@code 404 NOT_FOUND}.
   */
  @ExceptionHandler(RecursoNoEncontradoException.class)
  public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(
      RecursoNoEncontradoException ex, HttpServletRequest request) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
  }

  /**
   * Traduce {@link ReglaNegocioException} a {@code 409 CONFLICT}.
   */
  @ExceptionHandler(ReglaNegocioException.class)
  public ResponseEntity<ErrorResponse> handleReglaNegocio(
      ReglaNegocioException ex, HttpServletRequest request) {
    return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
  }

  /**
   * Traduce fallos de validación de {@code @Valid} en el body a {@code 400 BAD_REQUEST},
   * incluyendo el mapa campo → mensaje de error.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidacion(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> errores = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errores.put(error.getField(), error.getDefaultMessage());
    }
    return build(HttpStatus.BAD_REQUEST, "Validación fallida", request, errores);
  }

  /**
   * Traduce fallos de validación de {@code @RequestParam}/{@code @PathVariable} a
   * {@code 400 BAD_REQUEST}.
   */
  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidacionParametros(
      HandlerMethodValidationException ex, HttpServletRequest request) {
    return build(HttpStatus.BAD_REQUEST, "Validación fallida en parámetros de la request", request, null);
  }

  /**
   * Traduce un body JSON malformado o un valor de enum inválido a {@code 400 BAD_REQUEST}.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleJsonMalformado(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    return build(HttpStatus.BAD_REQUEST, "Cuerpo de la request malformado o ilegible", request, null);
  }

  /**
   * Traduce un parámetro de path o query con tipo incompatible (ej. id no numérico, enum
   * inexistente) a {@code 400 BAD_REQUEST}.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTipoInvalido(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String mensaje =
        "El parámetro '" + ex.getName() + "' tiene un valor inválido: " + ex.getValue();
    return build(HttpStatus.BAD_REQUEST, mensaje, request, null);
  }

  /**
   * Traduce una violación de restricción de integridad a nivel de base de datos (ej. unique
   * duplicado) a {@code 409 CONFLICT}.
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleIntegridad(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    return build(HttpStatus.CONFLICT, "Violación de una restricción de integridad de datos", request, null);
  }

  /**
   * Traduce una ruta inexistente a {@code 404 NOT_FOUND}, devolviendo JSON en vez de la página
   * Whitelabel por defecto.
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleRutaInexistente(
      NoResourceFoundException ex, HttpServletRequest request) {
    return build(HttpStatus.NOT_FOUND, "Recurso no encontrado: " + request.getRequestURI(), request, null);
  }

  /**
   * Fallback para cualquier otra excepción no controlada: responde {@code 500 INTERNAL_SERVER_ERROR}.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenerica(Exception ex, HttpServletRequest request) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request, null);
  }

  /**
   * Arma el {@link ErrorResponse} y lo envuelve en un {@link ResponseEntity} con el status dado.
   */
  private ResponseEntity<ErrorResponse> build(
      HttpStatus status, String mensaje, HttpServletRequest request, Map<String, String> errores) {
    ErrorResponse body =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            mensaje,
            request.getRequestURI(),
            errores);
    return ResponseEntity.status(status).body(body);
  }
}
