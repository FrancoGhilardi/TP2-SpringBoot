package com.tp2springboot.tp2_spring_boot.controller;

import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoDto;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioCreate;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioDto;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioEdit;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioPatch;
import com.tp2springboot.tp2_spring_boot.exception.ErrorResponse;
import com.tp2springboot.tp2_spring_boot.service.PedidoService;
import com.tp2springboot.tp2_spring_boot.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ABM de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "ABM de usuarios")
@Validated
public class UsuarioController {

  private final UsuarioService usuarioService;
  private final PedidoService pedidoService;

  public UsuarioController(UsuarioService usuarioService, PedidoService pedidoService) {
    this.usuarioService = usuarioService;
    this.pedidoService = pedidoService;
  }

  @GetMapping
  @Operation(summary = "Listar usuarios activos")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Listado obtenido")})
  public ResponseEntity<List<UsuarioDto>> listar() {
    return ResponseEntity.ok(usuarioService.listar());
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Buscar un usuario por id",
      description = "Además de devolver el usuario, imprime sus datos por consola (punto 7 de la consigna).")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<UsuarioDto> buscarPorId(@PathVariable Long id) {
    return ResponseEntity.ok(usuarioService.buscarPorId(id));
  }

  @GetMapping("/buscar")
  @Operation(
      summary = "Buscar un usuario por mail",
      description = "Además de devolver el usuario, imprime sus datos por consola (punto 8 de la consigna).")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
    @ApiResponse(
        responseCode = "400",
        description = "Mail no informado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<UsuarioDto> buscarPorMail(
      @Parameter(description = "Mail del usuario a buscar") @RequestParam @NotBlank String mail) {
    return ResponseEntity.ok(usuarioService.buscarPorMail(mail));
  }

  @GetMapping("/{id}/pedidos")
  @Operation(summary = "Listar los pedidos de un usuario")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Listado obtenido"),
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<PedidoDto>> listarPedidos(@PathVariable Long id) {
    usuarioService.obtenerEntidad(id);
    return ResponseEntity.ok(pedidoService.listarPorUsuario(id));
  }

  @PostMapping
  @Operation(summary = "Dar de alta un usuario")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Usuario creado"),
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe un usuario con ese mail",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody UsuarioCreate dto) {
    UsuarioDto creado = usuarioService.crear(dto);
    return ResponseEntity.created(URI.create("/api/usuarios/" + creado.id())).body(creado);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Editar un usuario existente")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe otro usuario con ese mail",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<UsuarioDto> editar(
      @PathVariable Long id, @Valid @RequestBody UsuarioEdit dto) {
    return ResponseEntity.ok(usuarioService.editar(id, dto));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Editar parcialmente un usuario existente")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe otro usuario con ese mail",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<UsuarioDto> patch(@PathVariable Long id, @RequestBody UsuarioPatch dto) {
    return ResponseEntity.ok(usuarioService.patch(id, dto));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Dar de baja lógica un usuario")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    usuarioService.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
