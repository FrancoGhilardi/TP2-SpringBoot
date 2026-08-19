package com.tp2springboot.tp2_spring_boot.config;

import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaCreate;
import com.tp2springboot.tp2_spring_boot.dto.categoria.CategoriaDto;
import com.tp2springboot.tp2_spring_boot.dto.detallePedido.DetallePedidoCreate;
import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoCreate;
import com.tp2springboot.tp2_spring_boot.dto.pedido.PedidoDto;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoCreate;
import com.tp2springboot.tp2_spring_boot.dto.producto.ProductoDto;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioCreate;
import com.tp2springboot.tp2_spring_boot.dto.usuario.UsuarioDto;
import com.tp2springboot.tp2_spring_boot.model.enums.EstadoPedido;
import com.tp2springboot.tp2_spring_boot.model.enums.FormaPago;
import com.tp2springboot.tp2_spring_boot.model.enums.Rol;
import com.tp2springboot.tp2_spring_boot.service.CategoriaService;
import com.tp2springboot.tp2_spring_boot.service.PedidoService;
import com.tp2springboot.tp2_spring_boot.service.ProductoService;
import com.tp2springboot.tp2_spring_boot.service.UsuarioService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Carga datos iniciales de demostración al arrancar la aplicación, instanciándolos siempre a
 * través de los servicios (nunca con {@code new Categoria(...)}), para ejercitar también las
 * validaciones. Apagado por defecto: la consigna pide persistir el juego de datos inicial desde
 * Postman, no desde un seeder. Se activa con {@code app.seed.enabled=true}.
 */
@Component
@Order(1)
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

  private final CategoriaService categoriaService;
  private final ProductoService productoService;
  private final UsuarioService usuarioService;
  private final PedidoService pedidoService;

  @Value("${app.seed.log-resumen:false}")
  private boolean logResumen;

  public DataSeeder(
      CategoriaService categoriaService,
      ProductoService productoService,
      UsuarioService usuarioService,
      PedidoService pedidoService) {
    this.categoriaService = categoriaService;
    this.productoService = productoService;
    this.usuarioService = usuarioService;
    this.pedidoService = pedidoService;
  }

  /**
   * Ejecuta el seed completo en orden (categorías → productos → usuarios → pedidos) y loguea un
   * resumen si {@code app.seed.log-resumen=true}.
   */
  @Override
  public void run(String... args) {
    List<CategoriaDto> categorias = seedCategorias();
    List<ProductoDto> productos = seedProductos(categorias);
    List<UsuarioDto> usuarios = seedUsuarios();
    List<PedidoDto> pedidos = seedPedidos(usuarios, productos);

    if (logResumen) {
      log.info(
          "Seed completo: {} categorías, {} productos, {} usuarios, {} pedidos",
          categorias.size(),
          productos.size(),
          usuarios.size(),
          pedidos.size());
    }
  }

  /** Da de alta las 3 categorías iniciales. */
  private List<CategoriaDto> seedCategorias() {
    return List.of(
        categoriaService.crear(new CategoriaCreate("Electrónica", "Gadgets y dispositivos")),
        categoriaService.crear(new CategoriaCreate("Indumentaria", "Ropa y calzado")),
        categoriaService.crear(new CategoriaCreate("Hogar", "Artículos para el hogar")));
  }

  /** Da de alta los 10 productos iniciales, repartidos entre las 3 categorías. */
  private List<ProductoDto> seedProductos(List<CategoriaDto> categorias) {
    Long electronica = categorias.get(0).id();
    Long indumentaria = categorias.get(1).id();
    Long hogar = categorias.get(2).id();

    return List.of(
        productoService.crear(
            new ProductoCreate("Mouse óptico", 4999.90, "Mouse inalámbrico", 50, null, true, electronica)),
        productoService.crear(
            new ProductoCreate("Teclado mecánico", 15999.0, "Switches azules", 30, null, true, electronica)),
        productoService.crear(
            new ProductoCreate("Monitor 24\"", 89999.0, "Full HD, 75Hz", 15, null, true, electronica)),
        productoService.crear(
            new ProductoCreate("Auriculares bluetooth", 12999.0, "Cancelación de ruido", 25, null, true, electronica)),
        productoService.crear(
            new ProductoCreate("Remera básica", 6500.0, "100% algodón", 60, null, true, indumentaria)),
        productoService.crear(
            new ProductoCreate("Pantalón jean", 18900.0, "Corte recto", 40, null, true, indumentaria)),
        productoService.crear(
            new ProductoCreate("Zapatillas urbanas", 34900.0, "Talles 38-45", 20, null, true, indumentaria)),
        productoService.crear(
            new ProductoCreate("Campera de abrigo", 42900.0, "Impermeable", 18, null, true, indumentaria)),
        productoService.crear(
            new ProductoCreate("Juego de sábanas", 21900.0, "Queen size", 22, null, true, hogar)),
        productoService.crear(
            new ProductoCreate("Set de ollas", 55900.0, "5 piezas antiadherentes", 12, null, true, hogar)));
  }

  /** Da de alta los 2 usuarios iniciales (uno ADMIN, uno USUARIO). */
  private List<UsuarioDto> seedUsuarios() {
    return List.of(
        usuarioService.crear(
            new UsuarioCreate("Ana", "Pérez", "ana.admin@example.com", "1122334455", "admin123", Rol.ADMIN)),
        usuarioService.crear(
            new UsuarioCreate("Juan", "Gómez", "juan.user@example.com", "1155667788", "user123", Rol.USUARIO)));
  }

  /**
   * Da de alta los 3 pedidos iniciales (2–3 detalles cada uno, forma de pago distinta), dejando
   * el primero en estado {@code TERMINADO} para que {@code totalFacturado()} no dé cero.
   */
  private List<PedidoDto> seedPedidos(List<UsuarioDto> usuarios, List<ProductoDto> productos) {
    Long admin = usuarios.get(0).id();
    Long user = usuarios.get(1).id();

    Long mouse = productos.get(0).id();
    Long teclado = productos.get(1).id();
    Long remera = productos.get(4).id();
    Long pantalon = productos.get(5).id();
    Long sabanas = productos.get(8).id();
    Long ollas = productos.get(9).id();

    PedidoDto pedido1 =
        pedidoService.crear(
            new PedidoCreate(
                user,
                FormaPago.TARJETA,
                List.of(
                    new DetallePedidoCreate(mouse, 2),
                    new DetallePedidoCreate(teclado, 1))));
    pedido1 = pedidoService.cambiarEstado(pedido1.id(), EstadoPedido.TERMINADO);

    PedidoDto pedido2 =
        pedidoService.crear(
            new PedidoCreate(
                admin,
                FormaPago.TRANSFERENCIA,
                List.of(
                    new DetallePedidoCreate(remera, 3),
                    new DetallePedidoCreate(pantalon, 1),
                    new DetallePedidoCreate(sabanas, 1))));

    PedidoDto pedido3 =
        pedidoService.crear(
            new PedidoCreate(
                user,
                FormaPago.EFECTIVO,
                List.of(
                    new DetallePedidoCreate(ollas, 1),
                    new DetallePedidoCreate(sabanas, 1))));

    return List.of(pedido1, pedido2, pedido3);
  }
}
