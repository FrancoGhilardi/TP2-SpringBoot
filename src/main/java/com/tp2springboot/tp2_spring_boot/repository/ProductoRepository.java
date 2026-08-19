package com.tp2springboot.tp2_spring_boot.repository;

import com.tp2springboot.tp2_spring_boot.model.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de {@link Producto}.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

  /** Devuelve todos los productos activos (no dados de baja). */
  List<Producto> findByEliminadoFalse();

  /** Busca un producto activo por id. */
  Optional<Producto> findByIdAndEliminadoFalse(Long id);

  /** Devuelve los productos activos de una categoría. */
  List<Producto> findByCategoriaIdAndEliminadoFalse(Long categoriaId);

  /** Devuelve los productos activos y disponibles para la venta. */
  List<Producto> findByDisponibleTrueAndEliminadoFalse();
}
