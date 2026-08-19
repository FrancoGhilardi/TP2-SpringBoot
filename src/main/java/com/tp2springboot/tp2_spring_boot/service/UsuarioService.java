package com.tp2springboot.tp2_spring_boot.service;

import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioCreate;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioDto;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioEdit;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioPatch;
import com.tp2springboot.tp2_spring_boot.exception.RecursoNoEncontradoException;
import com.tp2springboot.tp2_spring_boot.exception.ReglaNegocioException;
import com.tp2springboot.tp2_spring_boot.mapper.UsuarioMapper;
import com.tp2springboot.tp2_spring_boot.model.Usuario;
import com.tp2springboot.tp2_spring_boot.repository.UsuarioRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lógica de negocio para {@link Usuario}.
 */
@Service
public class UsuarioService {

  private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper usuarioMapper;

  public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
    this.usuarioRepository = usuarioRepository;
    this.usuarioMapper = usuarioMapper;
  }

  /**
   * Da de alta un usuario.
   *
   * @throws ReglaNegocioException si ya existe un usuario con el mismo mail
   */
  @Transactional
  public UsuarioDto crear(UsuarioCreate dto) {
    if (usuarioRepository.existsByMail(dto.mail())) {
      throw new ReglaNegocioException("Ya existe un usuario con el mail '" + dto.mail() + "'");
    }
    Usuario usuario = usuarioRepository.save(usuarioMapper.toEntity(dto));
    return usuarioMapper.toDto(usuario);
  }

  /**
   * Lista los usuarios activos.
   */
  @Transactional(readOnly = true)
  public List<UsuarioDto> listar() {
    return usuarioMapper.toDtoList(usuarioRepository.findByEliminadoFalse());
  }

  /**
   * Busca un usuario activo por id e imprime sus datos por consola (punto 7 de la consigna).
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional(readOnly = true)
  public UsuarioDto buscarPorId(Long id) {
    Usuario usuario = obtenerEntidad(id);
    logUsuario("búsqueda por id", usuario);
    return usuarioMapper.toDto(usuario);
  }

  /**
   * Busca un usuario activo por mail e imprime sus datos por consola (punto 8 de la consigna).
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional(readOnly = true)
  public UsuarioDto buscarPorMail(String mail) {
    Usuario usuario =
        usuarioRepository
            .findByMailAndEliminadoFalse(mail)
            .orElseThrow(
                () -> new RecursoNoEncontradoException("Usuario no encontrado: mail " + mail));
    logUsuario("búsqueda por mail", usuario);
    return usuarioMapper.toDto(usuario);
  }

  /**
   * Edita un usuario existente. No modifica la contraseña.
   *
   * @throws RecursoNoEncontradoException si no existe
   * @throws ReglaNegocioException si el nuevo mail ya lo usa otro usuario
   */
  @Transactional
  public UsuarioDto editar(Long id, UsuarioEdit dto) {
    Usuario usuario = obtenerEntidad(id);
    if (!dto.mail().equalsIgnoreCase(usuario.getMail()) && usuarioRepository.existsByMail(dto.mail())) {
      throw new ReglaNegocioException("Ya existe un usuario con el mail '" + dto.mail() + "'");
    }
    usuarioMapper.updateEntity(usuario, dto);
    return usuarioMapper.toDto(usuarioRepository.save(usuario));
  }

  /**
   * Edita parcialmente un usuario existente: solo se aplican los campos no nulos del patch. No
   * modifica la contraseña.
   *
   * @throws RecursoNoEncontradoException si no existe
   * @throws ReglaNegocioException si el nuevo mail ya lo usa otro usuario
   */
  @Transactional
  public UsuarioDto patch(Long id, UsuarioPatch dto) {
    Usuario usuario = obtenerEntidad(id);
    if (dto.mail() != null
        && !dto.mail().equalsIgnoreCase(usuario.getMail())
        && usuarioRepository.existsByMail(dto.mail())) {
      throw new ReglaNegocioException("Ya existe un usuario con el mail '" + dto.mail() + "'");
    }
    usuarioMapper.patchEntity(usuario, dto);
    return usuarioMapper.toDto(usuarioRepository.save(usuario));
  }

  /**
   * Da de baja lógica un usuario (nunca lo borra físicamente).
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  @Transactional
  public void eliminar(Long id) {
    Usuario usuario = obtenerEntidad(id);
    usuario.setEliminado(true);
    usuarioRepository.save(usuario);
  }

  /**
   * Resuelve la entidad {@link Usuario} activa por id, para uso interno de otros servicios.
   *
   * @throws RecursoNoEncontradoException si no existe
   */
  public Usuario obtenerEntidad(Long id) {
    return usuarioRepository
        .findByIdAndEliminadoFalse(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: id " + id));
  }

  /**
   * Imprime por consola los datos de un usuario encontrado (puntos 7 y 8 de la consigna).
   */
  private void logUsuario(String origen, Usuario usuario) {
    int cantidadPedidos = usuario.getPedidos() == null ? 0 : usuario.getPedidos().size();
    log.info(
        "Usuario encontrado ({}): id={}, nombre={}, apellido={}, mail={}, celular={}, rol={}, cantidadPedidos={}",
        origen,
        usuario.getId(),
        usuario.getNombre(),
        usuario.getApellido(),
        usuario.getMail(),
        usuario.getCelular(),
        usuario.getRol(),
        cantidadPedidos);
  }
}
