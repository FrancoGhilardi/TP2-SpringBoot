package com.tp2springboot.tp2_spring_boot.exception;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Respuesta de error estándar de la API")
public record ErrorResponse(
    @Schema(description = "Momento en que ocurrió el error") LocalDateTime timestamp,
    @Schema(description = "Código de estado HTTP", example = "404") int status,
    @Schema(description = "Nombre del estado HTTP", example = "Not Found") String error,
    @Schema(description = "Mensaje descriptivo del error", example = "Categoría no encontrada: id 99")
        String mensaje,
    @Schema(description = "Path del request que generó el error", example = "/api/categorias/99")
        String path,
    @Schema(description = "Mapa campo → mensaje, solo presente en errores 400 de validación")
        Map<String, String> errores) {}
