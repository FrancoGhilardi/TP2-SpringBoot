package com.tp2springboot.tp2_spring_boot.dto.usuario;

import com.tp2springboot.tp2_spring_boot.model.enums.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos de entrada para editar un usuario existente. El id viaja por el path. No incluye
 * contraseña.
 *
 * @param nombre nuevo nombre, obligatorio
 * @param apellido nuevo apellido, obligatorio
 * @param mail nuevo mail, obligatorio y con formato válido
 * @param celular nuevo teléfono de contacto, opcional
 * @param rol nuevo rol
 */
public record UsuarioEdit(
    @Schema(description = "Nuevo nombre de pila", example = "Ana") @NotBlank String nombre,
    @Schema(description = "Nuevo apellido", example = "Gómez") @NotBlank String apellido,
    @Schema(description = "Nuevo mail", example = "ana.gomez@example.com")
        @Email
        @NotBlank
        String mail,
    @Schema(description = "Nuevo teléfono de contacto", example = "1122334455")
        String celular,
    @Schema(description = "Nuevo rol", example = "USUARIO") Rol rol) {}
