package com.tp2springboot.tp2_spring_boot.dto.usuario;

import com.tp2springboot.tp2_spring_boot.model.enums.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos de entrada para dar de alta un usuario.
 *
 * @param nombre nombre de pila, obligatorio
 * @param apellido apellido, obligatorio
 * @param mail mail único, obligatorio y con formato válido
 * @param celular teléfono de contacto, opcional
 * @param contrasena contraseña, obligatoria, mínimo 6 caracteres
 * @param rol rol del usuario; si es {@code null} se asume {@link Rol#USUARIO}
 */
public record UsuarioCreate(
    @Schema(description = "Nombre de pila", example = "Ana") @NotBlank String nombre,
    @Schema(description = "Apellido", example = "Gómez") @NotBlank String apellido,
    @Schema(description = "Mail único del usuario", example = "ana.gomez@example.com")
        @Email
        @NotBlank
        String mail,
    @Schema(description = "Teléfono celular de contacto", example = "1122334455")
        String celular,
    @Schema(description = "Contraseña, mínimo 6 caracteres", example = "secreto1")
        @NotBlank
        @Size(min = 6)
        String contrasena,
    @Schema(description = "Rol del usuario; por defecto USUARIO", example = "USUARIO") Rol rol) {}
