package com.tp2springboot.tp2_spring_boot.controller;

import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoCreate;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoDto;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoEdit;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoPatch;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoStockPatch;
import com.tp2springboot.tp2_spring_boot.exception.ErrorResponse;
import com.tp2springboot.tp2_spring_boot.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ABM de productos.
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "ABM de productos")
public class ProductoController {

  private final ProductoService productoService;

  public ProductoController(ProductoService productoService) {
    this.productoService = productoService;
  }

  @GetMapping
  @Operation(
      summary = "Listar productos activos",
      description =
          "Si viene categoriaId, filtra por esa categoría. Si no, y viene disponible=true, "
              + "filtra por disponibilidad. Si no viene ninguno, lista todos los activos.")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Listado obtenido")})
  public ResponseEntity<List<ProductoDto>> listar(
      @Parameter(description = "Filtra por id de categoría") @RequestParam(required = false)
          Long categoriaId,
      @Parameter(description = "Filtra por disponibilidad para la venta")
          @RequestParam(required = false)
          Boolean disponible) {
    List<ProductoDto> productos;
    if (categoriaId != null) {
      productos = productoService.listarPorCategoria(categoriaId);
    } else if (Boolean.TRUE.equals(disponible)) {
      productos = productoService.listarDisponibles();
    } else {
      productos = productoService.listar();
    }
    return ResponseEntity.ok(productos);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar un producto por id")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Producto encontrado"),
    @ApiResponse(
        responseCode = "404",
        description = "Producto no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ProductoDto> buscarPorId(@PathVariable Long id) {
    return ResponseEntity.ok(productoService.buscarPorId(id));
  }

  @PostMapping
  @Operation(summary = "Dar de alta un producto")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Producto creado"),
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Categoría no encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ProductoDto> crear(@Valid @RequestBody ProductoCreate dto) {
    ProductoDto creado = productoService.crear(dto);
    return ResponseEntity.created(URI.create("/api/productos/" + creado.id())).body(creado);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Editar un producto existente")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Producto actualizado"),
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Producto o categoría no encontrados",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ProductoDto> editar(
      @PathVariable Long id, @Valid @RequestBody ProductoEdit dto) {
    return ResponseEntity.ok(productoService.editar(id, dto));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Editar parcialmente un producto existente")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Producto actualizado"),
    @ApiResponse(
        responseCode = "404",
        description = "Producto o categoría no encontrados",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ProductoDto> patch(@PathVariable Long id, @RequestBody ProductoPatch dto) {
    return ResponseEntity.ok(productoService.patch(id, dto));
  }

  @PatchMapping("/{id}/stock")
  @Operation(summary = "Actualizar el stock de un producto")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Stock actualizado"),
    @ApiResponse(
        responseCode = "400",
        description = "Stock inválido",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Producto no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ProductoDto> actualizarStock(
      @PathVariable Long id, @Valid @RequestBody ProductoStockPatch dto) {
    return ResponseEntity.ok(productoService.actualizarStock(id, dto.stock()));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Dar de baja lógica un producto")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Producto eliminado"),
    @ApiResponse(
        responseCode = "404",
        description = "Producto no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    productoService.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
