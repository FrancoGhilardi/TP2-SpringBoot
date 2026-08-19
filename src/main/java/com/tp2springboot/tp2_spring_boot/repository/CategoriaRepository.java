package com.tp2springboot.tp2_spring_boot.repository;

import com.tp2springboot.tp2_spring_boot.model.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de {@link Categoria}.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

  /** Devuelve todas las categorías activas (no dadas de baja). */
  List<Categoria> findByEliminadoFalse();

  /** Busca una categoría activa por id. */
  Optional<Categoria> findByIdAndEliminadoFalse(Long id);

  /** Indica si ya existe una categoría activa con ese nombre (case-insensitive). */
  boolean existsByNombreIgnoreCase(String nombre);
}
