package com.tp2springboot.tp2_spring_boot.controller;

import com.tp2springboot.tp2_spring_boot.dto.detallePedido.DetallePedidoCreate;
import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoCreate;
import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoDto;
import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoEdit;
import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoEstadoPatch;
import com.tp2springboot.tp2_spring_boot.exception.ErrorResponse;
import com.tp2springboot.tp2_spring_boot.model.enums.EstadoPedido;
import com.tp2springboot.tp2_spring_boot.service.PedidoService;
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
 * ABM de pedidos: alta transaccional con detalles, estados y descuento/reposición de stock.
 */
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "ABM de pedidos")
public class PedidoController {

  private final PedidoService pedidoService;

  public PedidoController(PedidoService pedidoService) {
    this.pedidoService = pedidoService;
  }

  @GetMapping
  @Operation(
      summary = "Listar pedidos activos",
      description =
          "Si viene usuarioId, filtra por ese usuario. Si no, y viene estado, filtra por estado. "
              + "Si no viene ninguno, lista todos los activos.")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Listado obtenido")})
  public ResponseEntity<List<PedidoDto>> listar(
      @Parameter(description = "Filtra por id de usuario") @RequestParam(required = false)
          Long usuarioId,
      @Parameter(description = "Filtra por estado del pedido") @RequestParam(required = false)
          EstadoPedido estado) {
    List<PedidoDto> pedidos;
    if (usuarioId != null) {
      pedidos = pedidoService.listarPorUsuario(usuarioId);
    } else if (estado != null) {
      pedidos = pedidoService.listarPorEstado(estado);
    } else {
      pedidos = pedidoService.listar();
    }
    return ResponseEntity.ok(pedidos);
  }

  @GetMapping("/total-facturado")
  @Operation(summary = "Sumar el total de los pedidos TERMINADO")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Total calculado")})
  public ResponseEntity<Double> totalFacturado() {
    return ResponseEntity.ok(pedidoService.totalFacturado());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar un pedido por id")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
    @ApiResponse(
        responseCode = "404",
        description = "Pedido no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PedidoDto> buscarPorId(@PathVariable Long id) {
    return ResponseEntity.ok(pedidoService.buscarPorId(id));
  }

  @PostMapping
  @Operation(summary = "Dar de alta un pedido con sus detalles")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Pedido creado"),
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos (sin detalles, cantidades inválidas, etc.)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Usuario o producto no encontrados",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Stock insuficiente en algún detalle",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PedidoDto> crear(@Valid @RequestBody PedidoCreate dto) {
    PedidoDto creado = pedidoService.crear(dto);
    return ResponseEntity.created(URI.create("/api/pedidos/" + creado.id())).body(creado);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Editar el estado y/o la forma de pago de un pedido")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Pedido actualizado"),
    @ApiResponse(
        responseCode = "404",
        description = "Pedido no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "El pedido ya está TERMINADO o CANCELADO",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PedidoDto> editar(@PathVariable Long id, @RequestBody PedidoEdit dto) {
    return ResponseEntity.ok(pedidoService.editar(id, dto));
  }

  @PatchMapping("/{id}/estado")
  @Operation(summary = "Cambiar el estado de un pedido")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Estado actualizado"),
    @ApiResponse(
        responseCode = "404",
        description = "Pedido no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "El pedido ya está TERMINADO o CANCELADO",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PedidoDto> cambiarEstado(
      @PathVariable Long id, @Valid @RequestBody PedidoEstadoPatch dto) {
    return ResponseEntity.ok(pedidoService.cambiarEstado(id, dto.estado()));
  }

  @PostMapping("/{id}/detalles")
  @Operation(summary = "Agregar un detalle a un pedido PENDIENTE")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Detalle agregado"),
    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Pedido o producto no encontrados",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "El pedido no está PENDIENTE o el stock es insuficiente",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PedidoDto> agregarDetalle(
      @PathVariable Long id, @Valid @RequestBody DetallePedidoCreate dto) {
    return ResponseEntity.ok(pedidoService.agregarDetalle(id, dto));
  }

  @DeleteMapping("/{id}/detalles/{productoId}")
  @Operation(summary = "Quitar de un pedido el detalle de un producto")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Detalle quitado"),
    @ApiResponse(
        responseCode = "404",
        description = "Pedido/producto no encontrados, o el pedido no tiene ese detalle",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PedidoDto> quitarDetalle(
      @PathVariable Long id, @PathVariable Long productoId) {
    return ResponseEntity.ok(pedidoService.quitarDetalle(id, productoId));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Dar de baja lógica un pedido")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Pedido eliminado"),
    @ApiResponse(
        responseCode = "404",
        description = "Pedido no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    pedidoService.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
