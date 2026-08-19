package com.tp2springboot.tp2_spring_boot.service;

import com.tp2springboot.tp2_spring_boot.dto.detallePedido.DetallePedidoCreate;
import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoCreate;
import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoDto;
import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoEdit;
import com.tp2springboot.tp2_spring_boot.exception.RecursoNoEncontradoException;
import com.tp2springboot.tp2_spring_boot.exception.ReglaNegocioException;
import com.tp2springboot.tp2_spring_boot.mapper.PedidoMapper;
import com.tp2springboot.tp2_spring_boot.model.DetallePedido;
import com.tp2springboot.tp2_spring_boot.model.Pedido;
import com.tp2springboot.tp2_spring_boot.model.Producto;
import com.tp2springboot.tp2_spring_boot.model.Usuario;
import com.tp2springboot.tp2_spring_boot.model.enums.EstadoPedido;
import com.tp2springboot.tp2_spring_boot.repository.PedidoRepository;
import com.tp2springboot.tp2_spring_boot.repository.ProductoRepository;
import com.tp2springboot.tp2_spring_boot.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lógica de negocio para {@link Pedido}: alta transaccional con detalles, cálculo de total,
 * descuento/reposición de stock y transiciones de estado.
 */
@Service
public class PedidoService {

  private final PedidoRepository pedidoRepository;
  private final UsuarioRepository usuarioRepository;
  private final ProductoRepository productoRepository;
  private final PedidoMapper pedidoMapper;

  public PedidoService(
      PedidoRepository pedidoRepository,
      UsuarioRepository usuarioRepository,
      ProductoRepository productoRepository,
      PedidoMapper pedidoMapper) {
    this.pedidoRepository = pedidoRepository;
    this.usuarioRepository = usuarioRepository;
    this.productoRepository = productoRepository;
    this.pedidoMapper = pedidoMapper;
  }

  /**
   * Da de alta un pedido con sus detalles en una única transacción: resuelve el usuario, valida
   * que haya al menos un detalle, y por cada detalle valida stock, lo agrega al pedido y
   * descuenta el stock del producto. Si cualquier detalle falla por stock insuficiente, se
   * revierte todo el pedido (incluido el stock ya descontado de detalles anteriores).
   *
   * @throws RecursoNoEncontradoException si el usuario o algún producto no existen
   * @throws ReglaNegocioException si no hay detalles o el stock de algún producto es insuficiente
   */
  @Transactional
  public PedidoDto crear(PedidoCreate dto) {
    Usuario usuario =
        usuarioRepository
            .findByIdAndEliminadoFalse(dto.usuarioId())
            .orElseThrow(
                () ->
                    new RecursoNoEncontradoException("Usuario no encontrado: id " + dto.usuarioId()));

    if (dto.detalles() == null || dto.detalles().isEmpty()) {
      throw new ReglaNegocioException("El pedido debe tener al menos un detalle");
    }

    Pedido pedido = Pedido.builder().formaPago(dto.formaPago()).build();
    usuario.addPedido(pedido);

    for (DetallePedidoCreate detalleCreate : dto.detalles()) {
      Producto producto = obtenerProducto(detalleCreate.productoId());
      if (producto.getStock() < detalleCreate.cantidad()) {
        throw new ReglaNegocioException(
            "Stock insuficiente para '" + producto.getNombre() + "': disponible " + producto.getStock());
      }
      pedido.addDetallePedido(detalleCreate.cantidad(), producto);
      producto.setStock(producto.getStock() - detalleCreate.cantidad());
      productoRepository.save(producto);
    }

    pedido.calcularTotal();
    return pedidoMapper.toDto(pedidoRepository.save(pedido));
  }

  /**
   * Lista los pedidos activos.
   */
  @Transactional(readOnly = true)
  public List<PedidoDto> listar() {
    return pedidoMapper.toDtoList(pedidoRepository.findByEliminadoFalse());
  }

  /**
   * Busca un pedido activo por id.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional(readOnly = true)
  public PedidoDto buscarPorId(Long id) {
    return pedidoMapper.toDto(obtenerEntidad(id));
  }

  /**
   * Lista los pedidos activos de un usuario.
   */
  @Transactional(readOnly = true)
  public List<PedidoDto> listarPorUsuario(Long usuarioId) {
    return pedidoMapper.toDtoList(pedidoRepository.findByUsuarioIdAndEliminadoFalse(usuarioId));
  }

  /**
   * Lista los pedidos activos en un estado determinado.
   */
  @Transactional(readOnly = true)
  public List<PedidoDto> listarPorEstado(EstadoPedido estado) {
    return pedidoMapper.toDtoList(pedidoRepository.findByEstadoAndEliminadoFalse(estado));
  }

  /**
   * Edita el estado y/o la forma de pago de un pedido. No permite salir de {@code TERMINADO} ni
   * de {@code CANCELADO}. Cancelar un pedido {@code PENDIENTE}/{@code CONFIRMADO} repone el
   * stock de todos sus detalles.
   *
   * @throws RecursoNoEncontradoException si el pedido no existe
   * @throws ReglaNegocioException si se intenta cambiar el estado de un pedido ya finalizado
   */
  @Transactional
  public PedidoDto editar(Long id, PedidoEdit dto) {
    Pedido pedido = obtenerEntidad(id);

    if (dto.estado() != null && dto.estado() != pedido.getEstado()) {
      aplicarTransicionEstado(pedido, dto.estado());
    }

    if (dto.formaPago() != null) {
      pedido.setFormaPago(dto.formaPago());
    }

    return pedidoMapper.toDto(pedidoRepository.save(pedido));
  }

  /**
   * Cambia el estado de un pedido. No permite salir de {@code TERMINADO} ni de {@code
   * CANCELADO}. Cancelar un pedido {@code PENDIENTE}/{@code CONFIRMADO} repone el stock de
   * todos sus detalles.
   *
   * @throws RecursoNoEncontradoException si el pedido no existe
   * @throws ReglaNegocioException si se intenta cambiar el estado de un pedido ya finalizado
   */
  @Transactional
  public PedidoDto cambiarEstado(Long id, EstadoPedido estado) {
    Pedido pedido = obtenerEntidad(id);
    aplicarTransicionEstado(pedido, estado);
    return pedidoMapper.toDto(pedidoRepository.save(pedido));
  }

  /**
   * Agrega un detalle a un pedido {@code PENDIENTE}, descontando el stock del producto.
   *
   * @throws RecursoNoEncontradoException si el pedido o el producto no existen
   * @throws ReglaNegocioException si el pedido no está {@code PENDIENTE} o el stock es insuficiente
   */
  @Transactional
  public PedidoDto agregarDetalle(Long pedidoId, DetallePedidoCreate dto) {
    Pedido pedido = obtenerEntidad(pedidoId);
    if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
      throw new ReglaNegocioException("Sólo se pueden agregar detalles a pedidos PENDIENTE");
    }
    Producto producto = obtenerProducto(dto.productoId());
    if (producto.getStock() < dto.cantidad()) {
      throw new ReglaNegocioException(
          "Stock insuficiente para '" + producto.getNombre() + "': disponible " + producto.getStock());
    }
    pedido.addDetallePedido(dto.cantidad(), producto);
    producto.setStock(producto.getStock() - dto.cantidad());
    productoRepository.save(producto);
    return pedidoMapper.toDto(pedidoRepository.save(pedido));
  }

  /**
   * Quita de un pedido el detalle correspondiente a un producto, devolviendo el stock.
   *
   * @throws RecursoNoEncontradoException si el pedido/producto no existen, o el pedido no tiene
   *     un detalle con ese producto
   */
  @Transactional
  public PedidoDto quitarDetalle(Long pedidoId, Long productoId) {
    Pedido pedido = obtenerEntidad(pedidoId);
    Producto producto = obtenerProducto(productoId);
    DetallePedido detalle = pedido.findDetallePedidoByProducto(producto);
    if (detalle == null) {
      throw new RecursoNoEncontradoException(
          "El pedido " + pedidoId + " no tiene un detalle con producto " + productoId);
    }
    producto.setStock(producto.getStock() + detalle.getCantidad());
    productoRepository.save(producto);
    pedido.deleteDetallePedidoByProducto(producto);
    return pedidoMapper.toDto(pedidoRepository.save(pedido));
  }

  /**
   * Da de baja lógica un pedido (nunca lo borra físicamente).
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional
  public void eliminar(Long id) {
    Pedido pedido = obtenerEntidad(id);
    pedido.setEliminado(true);
    pedidoRepository.save(pedido);
  }

  /**
   * Suma el total de todos los pedidos {@code TERMINADO}.
   */
  @Transactional(readOnly = true)
  public Double totalFacturado() {
    return pedidoRepository.totalFacturado(EstadoPedido.TERMINADO);
  }

  /**
   * Resuelve la entidad {@link Pedido} activa por id.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  public Pedido obtenerEntidad(Long id) {
    return pedidoRepository
        .findByIdAndEliminadoFalse(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: id " + id));
  }

  /**
   * Resuelve la entidad {@link Producto} activa por id.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  private Producto obtenerProducto(Long productoId) {
    return productoRepository
        .findByIdAndEliminadoFalse(productoId)
        .orElseThrow(
            () -> new RecursoNoEncontradoException("Producto no encontrado: id " + productoId));
  }

  /**
   * Aplica la transición al nuevo estado, con la guarda común a {@link #editar} y
   * {@link #cambiarEstado}: no se puede salir de {@code TERMINADO} ni de {@code CANCELADO}, y
   * pasar a {@code CANCELADO} repone el stock de todos los detalles.
   *
   * @throws ReglaNegocioException si el pedido ya está {@code TERMINADO} o {@code CANCELADO}
   */
  private void aplicarTransicionEstado(Pedido pedido, EstadoPedido nuevoEstado) {
    EstadoPedido estadoActual = pedido.getEstado();
    if (estadoActual == EstadoPedido.TERMINADO || estadoActual == EstadoPedido.CANCELADO) {
      throw new ReglaNegocioException("No se puede cambiar el estado de un pedido " + estadoActual);
    }
    if (nuevoEstado == EstadoPedido.CANCELADO) {
      reponerStock(pedido);
    }
    pedido.setEstado(nuevoEstado);
  }

  /**
   * Repone el stock de todos los productos de los detalles de un pedido (usado al cancelar).
   */
  private void reponerStock(Pedido pedido) {
    for (DetallePedido detalle : pedido.getDetalles()) {
      Producto producto = detalle.getProducto();
      producto.setStock(producto.getStock() + detalle.getCantidad());
      productoRepository.save(producto);
    }
  }
}
