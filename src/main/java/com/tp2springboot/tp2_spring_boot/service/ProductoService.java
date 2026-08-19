package com.tp2springboot.tp2_spring_boot.service;

import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoCreate;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoDto;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoEdit;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoPatch;
import com.tp2springboot.tp2_spring_boot.exception.RecursoNoEncontradoException;
import com.tp2springboot.tp2_spring_boot.exception.ReglaNegocioException;
import com.tp2springboot.tp2_spring_boot.mapper.ProductoMapper;
import com.tp2springboot.tp2_spring_boot.model.Categoria;
import com.tp2springboot.tp2_spring_boot.model.Producto;
import com.tp2springboot.tp2_spring_boot.repository.CategoriaRepository;
import com.tp2springboot.tp2_spring_boot.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lógica de negocio para {@link Producto}.
 */
@Service
public class ProductoService {

  private final ProductoRepository productoRepository;
  private final CategoriaRepository categoriaRepository;
  private final ProductoMapper productoMapper;

  public ProductoService(
      ProductoRepository productoRepository,
      CategoriaRepository categoriaRepository,
      ProductoMapper productoMapper) {
    this.productoRepository = productoRepository;
    this.categoriaRepository = categoriaRepository;
    this.productoMapper = productoMapper;
  }

  /**
   * Da de alta un producto, resolviendo su categoría por id.
   *
   * @throws RecursoNoEncontradoException si la categoría no existe
   */
  @Transactional
  public ProductoDto crear(ProductoCreate dto) {
    Categoria categoria = obtenerCategoria(dto.categoriaId());
    Producto producto = productoRepository.save(productoMapper.toEntity(dto, categoria));
    return productoMapper.toDto(producto);
  }

  /**
   * Lista los productos activos.
   */
  @Transactional(readOnly = true)
  public List<ProductoDto> listar() {
    return productoMapper.toDtoList(productoRepository.findByEliminadoFalse());
  }

  /**
   * Busca un producto activo por id.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional(readOnly = true)
  public ProductoDto buscarPorId(Long id) {
    return productoMapper.toDto(obtenerEntidad(id));
  }

  /**
   * Lista los productos activos de una categoría.
   */
  @Transactional(readOnly = true)
  public List<ProductoDto> listarPorCategoria(Long categoriaId) {
    return productoMapper.toDtoList(
        productoRepository.findByCategoriaIdAndEliminadoFalse(categoriaId));
  }

  /**
   * Lista los productos activos y disponibles para la venta.
   */
  @Transactional(readOnly = true)
  public List<ProductoDto> listarDisponibles() {
    return productoMapper.toDtoList(productoRepository.findByDisponibleTrueAndEliminadoFalse());
  }

  /**
   * Edita un producto existente, resolviendo su nueva categoría por id.
   *
   * @throws RecursoNoEncontradoException si el producto o la categoría no existen
   */
  @Transactional
  public ProductoDto editar(Long id, ProductoEdit dto) {
    Producto producto = obtenerEntidad(id);
    Categoria categoria = obtenerCategoria(dto.categoriaId());
    productoMapper.updateEntity(producto, dto, categoria);
    return productoMapper.toDto(productoRepository.save(producto));
  }

  /**
   * Edita parcialmente un producto existente: solo se aplican los campos no nulos del patch. La
   * categoría solo se resuelve y se pisa si {@code categoriaId} viene en el patch.
   *
   * @throws RecursoNoEncontradoException si el producto o la categoría no existen
   */
  @Transactional
  public ProductoDto patch(Long id, ProductoPatch dto) {
    Producto producto = obtenerEntidad(id);
    Categoria categoria = dto.categoriaId() != null ? obtenerCategoria(dto.categoriaId()) : null;
    productoMapper.patchEntity(producto, dto, categoria);
    return productoMapper.toDto(productoRepository.save(producto));
  }

  /**
   * Actualiza el stock de un producto al valor exacto indicado.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional
  public ProductoDto actualizarStock(Long id, Integer stock) {
    Producto producto = obtenerEntidad(id);
    producto.setStock(stock);
    return productoMapper.toDto(productoRepository.save(producto));
  }

  /**
   * Da de baja lógica un producto (nunca lo borra físicamente).
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional
  public void eliminar(Long id) {
    Producto producto = obtenerEntidad(id);
    producto.setEliminado(true);
    productoRepository.save(producto);
  }

  /**
   * Descuenta stock de un producto ya resuelto.
   *
   * @throws ReglaNegocioException si el stock disponible es menor a la cantidad solicitada
   */
  @Transactional
  public void descontarStock(Producto producto, int cantidad) {
    if (producto.getStock() < cantidad) {
      throw new ReglaNegocioException(
          "Stock insuficiente para '" + producto.getNombre() + "': disponible " + producto.getStock());
    }
    producto.setStock(producto.getStock() - cantidad);
    productoRepository.save(producto);
  }

  /**
   * Resuelve la entidad {@link Producto} activa por id, para uso interno de otros servicios.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  public Producto obtenerEntidad(Long id) {
    return productoRepository
        .findByIdAndEliminadoFalse(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: id " + id));
  }

  /**
   * Resuelve la entidad {@link Categoria} activa por id.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  private Categoria obtenerCategoria(Long categoriaId) {
    return categoriaRepository
        .findByIdAndEliminadoFalse(categoriaId)
        .orElseThrow(
            () -> new RecursoNoEncontradoException("Categoría no encontrada: id " + categoriaId));
  }
}
