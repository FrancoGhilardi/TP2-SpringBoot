package com.tp2springboot.tp2_spring_boot.dto.usuario;

import com.tp2springboot.tp2_spring_boot.model.enums.Rol;
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
    @NotBlank String nombre,
    @NotBlank String apellido,
    @Email @NotBlank String mail,
    String celular,
    @NotBlank @Size(min = 6) String contrasena,
    Rol rol) {}
