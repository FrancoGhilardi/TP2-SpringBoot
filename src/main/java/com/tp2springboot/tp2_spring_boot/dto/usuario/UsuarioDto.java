package com.tp2springboot.tp2_spring_boot.dto.usuario;

import com.tp2springboot.tp2_spring_boot.model.enums.Rol;

/**
 * Representación de un usuario hacia el cliente. Nunca incluye la contraseña.
 *
 * @param id identificador
 * @param nombre nombre de pila
 * @param apellido apellido
 * @param mail mail único
 * @param celular teléfono de contacto
 * @param rol rol del usuario
 * @param cantidadPedidos cantidad de pedidos realizados (derivado)
 */
public record UsuarioDto(
    Long id,
    String nombre,
    String apellido,
    String mail,
    String celular,
    Rol rol,
    int cantidadPedidos) {}
