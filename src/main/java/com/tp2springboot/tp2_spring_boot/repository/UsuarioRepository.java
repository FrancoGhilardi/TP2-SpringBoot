package com.tp2springboot.tp2_spring_boot.repository;

import com.tp2springboot.tp2_spring_boot.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de {@link Usuario}.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  /** Devuelve todos los usuarios activos (no dados de baja). */
  List<Usuario> findByEliminadoFalse();

  /** Busca un usuario activo por id. */
  Optional<Usuario> findByIdAndEliminadoFalse(Long id);

  /** Busca un usuario activo por mail. */
  Optional<Usuario> findByMailAndEliminadoFalse(String mail);

  /** Indica si ya existe un usuario con ese mail. */
  boolean existsByMail(String mail);
}
