package com.tp2springboot.tp2_spring_boot.dto.usuario;

import com.tp2springboot.tp2_spring_boot.model.enums.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

/**
 * Datos de entrada para editar parcialmente un usuario existente. Todos los campos son
 * opcionales; solo se aplican los que llegan no nulos. No incluye contraseña.
 *
 * @param nombre nuevo nombre, opcional
 * @param apellido nuevo apellido, opcional
 * @param mail nuevo mail, opcional y con formato válido si se envía
 * @param celular nuevo teléfono de contacto, opcional
 * @param rol nuevo rol, opcional
 */
public record UsuarioPatch(
    @Schema(description = "Nuevo nombre de pila", example = "Ana") String nombre,
    @Schema(description = "Nuevo apellido", example = "Gómez") String apellido,
    @Schema(description = "Nuevo mail", example = "ana.gomez@example.com") @Email String mail,
    @Schema(description = "Nuevo teléfono de contacto", example = "1122334455") String celular,
    @Schema(description = "Nuevo rol", example = "USUARIO") Rol rol) {}
