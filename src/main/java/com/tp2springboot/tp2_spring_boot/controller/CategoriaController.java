package com.tp2springboot.tp2_spring_boot.controller;

import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaCreate;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaDto;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaEdit;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaPatch;
import com.tp2springboot.tp2_spring_boot.exception.ErrorResponse;
import com.tp2springboot.tp2_spring_boot.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ABM de categorías de productos.
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "ABM de categorías de productos")
public class CategoriaController {

  private final CategoriaService categoriaService;

  public CategoriaController(CategoriaService categoriaService) {
    this.categoriaService = categoriaService;
  }

  @GetMapping
  @Operation(summary = "Listar categorías activas")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Listado obtenido")})
  public ResponseEntity<List<CategoriaDto>> listar() {
    return ResponseEntity.ok(categoriaService.listar());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar una categoría por id")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
    @ApiResponse(
        responseCode = "404",
        description = "Categoría no encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<CategoriaDto> buscarPorId(@PathVariable Long id) {
    return ResponseEntity.ok(categoriaService.buscarPorId(id));
  }

  @PostMapping
  @Operation(summary = "Dar de alta una categoría")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Categoría creada"),
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe una categoría con ese nombre",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<CategoriaDto> crear(@Valid @RequestBody CategoriaCreate dto) {
    CategoriaDto creada = categoriaService.crear(dto);
    return ResponseEntity.created(URI.create("/api/categorias/" + creada.id())).body(creada);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Editar una categoría existente")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Categoría no encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<CategoriaDto> editar(
      @PathVariable Long id, @Valid @RequestBody CategoriaEdit dto) {
    return ResponseEntity.ok(categoriaService.editar(id, dto));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Editar parcialmente una categoría existente")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
    @ApiResponse(
        responseCode = "404",
        description = "Categoría no encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe otra categoría con ese nombre",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<CategoriaDto> patch(
      @PathVariable Long id, @RequestBody CategoriaPatch dto) {
    return ResponseEntity.ok(categoriaService.patch(id, dto));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Dar de baja lógica una categoría")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
    @ApiResponse(
        responseCode = "404",
        description = "Categoría no encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    categoriaService.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
