package com.tp2springboot.tp2_spring_boot.service;

import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaCreate;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaDto;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaEdit;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaPatch;
import com.tp2springboot.tp2_spring_boot.exception.RecursoNoEncontradoException;
import com.tp2springboot.tp2_spring_boot.exception.ReglaNegocioException;
import com.tp2springboot.tp2_spring_boot.mapper.CategoriaMapper;
import com.tp2springboot.tp2_spring_boot.model.Categoria;
import com.tp2springboot.tp2_spring_boot.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lógica de negocio para {@link Categoria}.
 */
@Service
public class CategoriaService {

  private final CategoriaRepository categoriaRepository;
  private final CategoriaMapper categoriaMapper;

  public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
    this.categoriaRepository = categoriaRepository;
    this.categoriaMapper = categoriaMapper;
  }

  /**
   * Da de alta una categoría.
   *
   * @throws ReglaNegocioException si ya existe una categoría con el mismo nombre
   */
  @Transactional
  public CategoriaDto crear(CategoriaCreate dto) {
    if (categoriaRepository.existsByNombreIgnoreCase(dto.nombre())) {
      throw new ReglaNegocioException("Ya existe una categoría con el nombre '" + dto.nombre() + "'");
    }
    Categoria categoria = categoriaRepository.save(categoriaMapper.toEntity(dto));
    return categoriaMapper.toDto(categoria);
  }

  /**
   * Lista las categorías activas.
   */
  @Transactional(readOnly = true)
  public List<CategoriaDto> listar() {
    return categoriaMapper.toDtoList(categoriaRepository.findByEliminadoFalse());
  }

  /**
   * Busca una categoría activa por id.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional(readOnly = true)
  public CategoriaDto buscarPorId(Long id) {
    return categoriaMapper.toDto(obtenerEntidad(id));
  }

  /**
   * Edita una categoría existente.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional
  public CategoriaDto editar(Long id, CategoriaEdit dto) {
    Categoria categoria = obtenerEntidad(id);
    categoriaMapper.updateEntity(categoria, dto);
    return categoriaMapper.toDto(categoriaRepository.save(categoria));
  }

  /**
   * Edita parcialmente una categoría existente: solo se aplican los campos no nulos del patch.
   *
   * @throws RecursoNoEncontradoException si no existe
   * @throws ReglaNegocioException si el nuevo nombre ya lo usa otra categoría
   */
  @Transactional
  public CategoriaDto patch(Long id, CategoriaPatch dto) {
    Categoria categoria = obtenerEntidad(id);
    if (dto.nombre() != null
        && !dto.nombre().equalsIgnoreCase(categoria.getNombre())
        && categoriaRepository.existsByNombreIgnoreCase(dto.nombre())) {
      throw new ReglaNegocioException("Ya existe una categoría con el nombre '" + dto.nombre() + "'");
    }
    categoriaMapper.patchEntity(categoria, dto);
    return categoriaMapper.toDto(categoriaRepository.save(categoria));
  }

  /**
   * Da de baja lógica una categoría (nunca la borra físicamente).
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional
  public void eliminar(Long id) {
    Categoria categoria = obtenerEntidad(id);
    categoria.setEliminado(true);
    categoriaRepository.save(categoria);
  }

  /**
   * Resuelve la entidad {@link Categoria} activa por id, para uso interno de otros servicios.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  public Categoria obtenerEntidad(Long id) {
    return categoriaRepository
        .findByIdAndEliminadoFalse(id)
        .orElseThrow(
            () -> new RecursoNoEncontradoException("Categoría no encontrada: id " + id));
  }
}
