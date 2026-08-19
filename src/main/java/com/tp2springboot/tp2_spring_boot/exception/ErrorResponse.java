package com.tp2springboot.tp2_spring_boot.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Cuerpo de respuesta estándar para errores HTTP.
 *
 * @param timestamp momento en que ocurrió el error
 * @param status código de estado HTTP
 * @param error nombre del estado HTTP (ej. "Not Found")
 * @param mensaje mensaje descriptivo del error
 * @param path path del request que generó el error
 * @param errores mapa campo→mensaje para errores de validación; {@code null} si no aplica
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String mensaje,
    String path,
    Map<String, String> errores) {}
