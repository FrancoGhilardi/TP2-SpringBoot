package com.tp2springboot.tp2_spring_boot.dto.usuario;

import com.tp2springboot.tp2_spring_boot.model.enums.Rol;
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
    @NotBlank String nombre,
    @NotBlank String apellido,
    @Email @NotBlank String mail,
    String celular,
    Rol rol) {}
