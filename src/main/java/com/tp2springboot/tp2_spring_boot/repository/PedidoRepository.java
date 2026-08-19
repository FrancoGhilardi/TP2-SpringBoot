package com.tp2springboot.tp2_spring_boot.repository;

import com.tp2springboot.tp2_spring_boot.model.Pedido;
import com.tp2springboot.tp2_spring_boot.model.enums.EstadoPedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de {@link Pedido}.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

  /** Devuelve todos los pedidos activos (no dados de baja). */
  List<Pedido> findByEliminadoFalse();

  /** Busca un pedido activo por id. */
  Optional<Pedido> findByIdAndEliminadoFalse(Long id);

  /** Devuelve los pedidos activos de un usuario. */
  List<Pedido> findByUsuarioIdAndEliminadoFalse(Long usuarioId);

  /** Devuelve los pedidos activos en un estado determinado. */
  List<Pedido> findByEstadoAndEliminadoFalse(EstadoPedido estado);

  /**
   * Suma el total de los pedidos activos en un estado determinado (0 si no hay ninguno).
   */
  @Query(
      "SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estado = :estado AND p.eliminado = false")
  Double totalFacturado(@Param("estado") EstadoPedido estado);
}
