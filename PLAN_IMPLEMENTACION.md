# Plan de implementación — TP API REST (tp2-spring-boot)

Migración del comportamiento de `tp1springboot` (sistema de gestión de pedidos, consola) a
`tp2-spring-boot`, adaptándolo a la consigna del TP de API REST.

---

## 0. Contexto y decisiones de diseño

### Qué pide la consigna (PDF `TP1_Api_Rest_Programacion_IV.pdf`)

Continuando con las clases de la práctica de JPA (el UML es exactamente el modelo que ya
existe en `tp1springboot`):

1. Agregar dependencia de Swagger (`org.springdoc:springdoc-openapi-starter-webmvc-ui`).
2. Desarrollar capa Repository.
3. Desarrollar capa Service.
4. Desarrollar capa Controller y AdviceController.
5. Crear y persistir a partir de DTOs usando Postman: 2 usuarios, 3 pedidos (al menos 2 detalles
   cada uno), 3 categorías, 10 productos.
6. Actualizar 1 categoría.
7. Buscar usuarios por id y mostrar información por consola.
8. Buscar usuarios por mail y mostrar por consola.
9. Mostrar Swagger funcionando.

Métodos HTTP exigidos: **GET, POST, PUT, PATCH, DELETE** (el PDF los enumera explícitamente;
`tp1springboot` no tiene PATCH, así que se agrega en este proyecto).

### UML del PDF vs. modelo de tp1springboot

El UML coincide 1:1 con `tp1springboot/src/main/java/.../model`:

- `Base` (abstracta): `id: Long`, `eliminado: boolean`, `createdAt: LocalDateTime`.
- `Calculable` (interface): `calcularTotal(): void`.
- `Usuario`: nombre, apellido, mail, celular, contraseña, rol → `1..m` `Pedido`.
- `Pedido` implements `Calculable`: fecha, estado, total, formaPago + `addDetallePedido`,
  `findDetallePedidoByProducto`, `deleteDetallePedidoByProducto` → composición `1..m` `DetallePedido`.
- `DetallePedido`: cantidad, subtotal → `m..1` `Producto`.
- `Producto`: nombre, precio, descripcion, stock, imagen, disponible → `m..1` `Categoria`.
- `Categoria`: nombre, descripcion.
- Enums: `FormaPago` (TARJETA, TRANSFERENCIA, EFECTIVO), `Rol` (ADMIN, USUARIO),
  `Estado` (PENDIENTE, CONFIRMADO, TERMINADO, CANCELADO) — en tp1 se llama `EstadoPedido`,
  se mantiene ese nombre por claridad.

**Conclusión:** el modelo se migra tal cual, sin cambios de dominio.

### Qué se migra, qué se adapta y qué se descarta

| Elemento de tp1springboot | En tp2-spring-boot |
|---|---|
| `model/` (entidades, enums, `Base`, `Calculable`) | Se migra idéntico (cambia el package) |
| `repository/` (4 interfaces) | Se migra + se agregan derived queries para filtros de la API |
| `service/` (4 servicios) | Se migra la lógica; cambia la firma de los `editar(...)` (el id viaja por path) y se agregan métodos `patch` |
| `mapper/` (5 mappers manuales) | Se migra idéntico |
| `exception/` (2 excepciones + `ErrorResponse` + `GlobalExceptionHandler`) | Se migra y se amplía (más handlers: tipo de parámetro inválido, JSON malformado, violación de integridad, ruta inexistente) |
| `dto/` | Se migra y se reorganiza: se separa `PedidoCreate` de `PedidoDto`, se quita `id` de los DTOs `*Edit`, se agregan DTOs `*Patch` |
| `config/DataSeeder` | Se migra pero **deshabilitado por defecto** (`app.seed.enabled=false`): la consigna pide persistir desde Postman |
| `console/` (menú interactivo `ConsoleMenu`, `*Menu`, `ConsoleIO`) | **No se migra.** El proyecto es una API REST. Los puntos 7 y 8 de la consigna ("mostrar por consola") se resuelven logueando por consola desde `UsuarioService` al buscar por id y por mail |
| `Tp1springbootApplication` (forzado de UTF-8 en `System.out`) | Se migra a `Tp2SpringBootApplication`: sigue siendo necesario en Windows para que los logs de los puntos 7 y 8 muestren bien á, é, ñ |
| — | **Nuevo:** capa `controller/` (4 controllers REST) |
| — | **Nuevo:** `config/OpenApiConfig` + anotaciones springdoc |

### Convenciones que se mantienen de tp1springboot

- **Inyección por constructor únicamente** (campos `final`, sin `@Autowired`, sin field injection).
- **Baja lógica siempre**: `DELETE` marca `eliminado = true`; nunca borrado físico. Las queries
  activas usan `findByIdAndEliminadoFalse` / `findByEliminadoFalse`.
- **Relaciones bidireccionales**: el FK vive en el lado hijo (`@ManyToOne`), el padre usa `mappedBy`,
  y los helpers de entidad (`addDetallePedido`, `addPedido`, `addProducto`) sincronizan ambos lados.
- **DTOs como `record`**, agrupados por agregado en `dto/<agregado>/`.
- **Mappers manuales** `@Component` (sin MapStruct/ModelMapper).
- **Servicios como frontera transaccional**: lecturas `@Transactional(readOnly = true)`,
  escrituras `@Transactional`. Los controllers **nunca** ven entidades, solo DTOs.
- **Excepciones de dominio**: `RecursoNoEncontradoException` (404) y `ReglaNegocioException` (409),
  traducidas centralmente por `GlobalExceptionHandler`.
- Javadoc en español en todas las clases públicas.

### Contrato REST (definido acá, implementado en las fases 7–10)

Base path: `/api`.

| Recurso | Método | Ruta | Éxito |
|---|---|---|---|
| Categorías | GET | `/api/categorias` | 200 |
| | GET | `/api/categorias/{id}` | 200 |
| | POST | `/api/categorias` | 201 + `Location` |
| | PUT | `/api/categorias/{id}` | 200 |
| | PATCH | `/api/categorias/{id}` | 200 |
| | DELETE | `/api/categorias/{id}` | 204 |
| Productos | GET | `/api/productos` (`?categoriaId=`, `?disponible=`) | 200 |
| | GET | `/api/productos/{id}` | 200 |
| | POST | `/api/productos` | 201 + `Location` |
| | PUT | `/api/productos/{id}` | 200 |
| | PATCH | `/api/productos/{id}` | 200 |
| | PATCH | `/api/productos/{id}/stock` | 200 |
| | DELETE | `/api/productos/{id}` | 204 |
| Usuarios | GET | `/api/usuarios` | 200 |
| | GET | `/api/usuarios/{id}` | 200 |
| | GET | `/api/usuarios/buscar?mail=` | 200 |
| | GET | `/api/usuarios/{id}/pedidos` | 200 |
| | POST | `/api/usuarios` | 201 + `Location` |
| | PUT | `/api/usuarios/{id}` | 200 |
| | PATCH | `/api/usuarios/{id}` | 200 |
| | DELETE | `/api/usuarios/{id}` | 204 |
| Pedidos | GET | `/api/pedidos` (`?usuarioId=`, `?estado=`) | 200 |
| | GET | `/api/pedidos/{id}` | 200 |
| | GET | `/api/pedidos/total-facturado` | 200 |
| | POST | `/api/pedidos` | 201 + `Location` |
| | PUT | `/api/pedidos/{id}` | 200 |
| | PATCH | `/api/pedidos/{id}/estado` | 200 |
| | POST | `/api/pedidos/{id}/detalles` | 200 |
| | DELETE | `/api/pedidos/{id}/detalles/{productoId}` | 200 |
| | DELETE | `/api/pedidos/{id}` | 204 |

Códigos de error uniformes: `400` validación / parámetro inválido / JSON malformado,
`404` recurso inexistente, `409` violación de regla de negocio, `500` fallo no controlado.

### Riesgo conocido a resolver en la Fase 1 (leer antes de empezar)

La consigna indica `springdoc-openapi-starter-webmvc-ui:2.5.0`, pero el proyecto está sobre
**Spring Boot 4.1.0** (Spring Framework 7). La línea `springdoc 2.x` fue construida contra
Spring Boot 3.x / Framework 6; la última publicada en Maven Central es **2.8.6**. Es posible que
springdoc 2.x no arranque sobre Boot 4.

La Fase 1 contempla las dos rutas (`1.A` intentar springdoc sobre Boot 4, `1.B` contingencia:
bajar el proyecto a Spring Boot 3.5.x, que es el escenario para el que la consigna fue escrita).
Cuál se aplica se decide con el resultado del arranque en la Fase 1.A — no antes.

### Paquete raíz

`com.tp2springboot.tp2_spring_boot` (el que ya generó Spring Initializr; no se renombra).
Todas las rutas de archivos de abajo son relativas a
`tp2-spring-boot/src/main/java/com/tp2springboot/tp2_spring_boot/`.

### Orden de las fases

```
0 → 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 ─┬→ 9  → 10 → 11 → 12 → 13
                                    └→ (9 y 10 dependen de 6 y 8)
```

Cada fase deja el proyecto **compilando y arrancando**. Ninguna fase incluye tests ni comandos de git.

---

## Fase 0 — Dependencias, configuración base y arranque

**Objetivo:** dejar el esqueleto de `tp2-spring-boot` con las mismas capacidades de infraestructura
que `tp1springboot` (validación, H2, JPA, UTF-8), sin todavía escribir dominio.

### Precondiciones

- `tp2-spring-boot/` es el skeleton de Spring Initializr: solo `Tp2SpringBootApplication` y el test
  generado.
- `build.gradle` ya tiene: `spring-boot-h2console`, `spring-boot-starter-data-jpa`,
  `spring-boot-starter-webmvc`, `lombok`, `devtools`, `h2`.
- JDK 21 disponible (toolchain ya declarado).

### Implementación

1. **`build.gradle`** — agregar la dependencia que falta respecto de tp1:
   ```gradle
   implementation 'org.springframework.boot:spring-boot-starter-validation'
   ```
   (Swagger se agrega en la Fase 1, para poder aislar el problema de compatibilidad.)

2. **`src/main/resources/application.properties`** — reemplazar el contenido por la configuración
   equivalente a la de tp1, adaptada:
   ```properties
   # App
   spring.application.name=tp2-spring-boot
   server.port=8080

   # Datasource H2
   spring.datasource.url=jdbc:h2:mem:pedidosdb
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=

   # Consola H2
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console

   # JPA/Hibernate
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   spring.jpa.open-in-view=false

   # Serialización JSON: no emitir campos nulos en las respuestas
   spring.jackson.default-property-inclusion=non_null

   # Seed de datos (la consigna pide cargar desde Postman -> apagado por defecto)
   app.seed.enabled=false
   app.seed.log-resumen=true

   # Logging
   logging.level.com.tp2springboot=DEBUG
   ```

3. **`Tp2SpringBootApplication.java`** — portar el forzado de UTF-8 de
   `Tp1springbootApplication.main` (necesario en Windows para que la salida por consola de los
   puntos 7 y 8 de la consigna muestre correctamente á/é/ñ):
   ```java
   public static void main(String[] args) {
     System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
     System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
     SpringApplication.run(Tp2SpringBootApplication.class, args);
   }
   ```

### Criterios de aceptación

- `./gradlew build -x test` compila sin errores.
- La aplicación arranca y loguea `Tomcat started on port 8080`.
- `http://localhost:8080/h2-console` responde y permite conectarse con
  `jdbc:h2:mem:pedidosdb` / `sa` / (password vacío).
- `spring-boot-starter-validation` aparece en el classpath (`./gradlew dependencies --configuration runtimeClasspath` lo lista).
- `application.properties` no contiene ninguna property con prefijo `app.console.*` (no hay menú de consola en este proyecto).

---

## Fase 1 — Swagger / OpenAPI operativo

**Objetivo:** dejar `/swagger-ui` accesible antes de escribir controllers, para que cada fase
posterior se pueda verificar visualmente.

### Precondiciones

- Fase 0 completa: la app arranca.

### Implementación

**1.A — Ruta principal (springdoc sobre Spring Boot 4.1.0)**

1. Agregar a `build.gradle`:
   ```gradle
   implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6'
   ```
   Se usa `2.8.6` (última publicada de la línea 2.x) en lugar del `2.5.0` que indica el PDF: misma
   línea de la dependencia pedida, con las correcciones de compatibilidad más recientes.

2. Agregar a `application.properties`:
   ```properties
   springdoc.swagger-ui.path=/swagger-ui.html
   springdoc.api-docs.path=/v3/api-docs
   springdoc.swagger-ui.operationsSorter=method
   springdoc.swagger-ui.tagsSorter=alpha
   ```

3. Crear `config/OpenApiConfig.java` (`@Configuration`) con un `@Bean OpenAPI` que declare:
   título "API REST — Sistema de Gestión de Pedidos", versión `1.0.0`, descripción breve, y un
   `Server` apuntando a `http://localhost:8080`.

4. Arrancar la aplicación y abrir `http://localhost:8080/swagger-ui.html`.

**1.B — Contingencia (solo si 1.A falla)**

Si la aplicación **no arranca** o `/v3/api-docs` responde 404/500 por incompatibilidad de springdoc
2.x con Spring Framework 7:

1. En `build.gradle`, bajar el plugin de Spring Boot a la última 3.5.x:
   ```gradle
   id 'org.springframework.boot' version '3.5.9'
   ```
2. Revertir los nombres de starters de Boot 4 a los de Boot 3:
   - `spring-boot-starter-webmvc` → `spring-boot-starter-web`
   - `spring-boot-h2console` → (se elimina; en Boot 3 la consola H2 viene con `spring-boot-starter-web` + `spring.h2.console.enabled=true`)
   - `spring-boot-starter-data-jpa-test` / `spring-boot-starter-webmvc-test` → `spring-boot-starter-test`
3. Volver a levantar y verificar Swagger.

En cualquiera de las dos rutas, dejar registrada la decisión (versión de Boot y de springdoc
efectivamente usadas) en el `README.md` del proyecto — se completa en la Fase 13.

### Criterios de aceptación

- La aplicación arranca sin excepciones en el log de inicio.
- `GET http://localhost:8080/v3/api-docs` devuelve `200` con un JSON OpenAPI válido
  (`"openapi": "3.x.x"`).
- `http://localhost:8080/swagger-ui.html` renderiza la UI de Swagger con el título y la versión
  definidos en `OpenApiConfig`.
- La UI no muestra endpoints todavía (aún no hay controllers) — eso es lo esperado en esta fase.
- Queda anotado en el `README.md` (o en un comentario del `build.gradle`) qué ruta se aplicó (1.A o 1.B).

---

## Fase 2 — Capa `model` (entidades, enums, `Base`, `Calculable`)

**Objetivo:** portar el modelo de dominio del UML tal como está en `tp1springboot`.

### Precondiciones

- Fase 0 completa (JPA + H2 configurados).

### Implementación

Crear el paquete `model/` y `model/enums/` copiando desde
`tp1springboot/src/main/java/com/tp1springboot/tp1springboot/model/`, cambiando únicamente
la declaración `package` y los `import`:

1. **`model/enums/Rol.java`** — `ADMIN`, `USUARIO`.
2. **`model/enums/FormaPago.java`** — `TARJETA`, `TRANSFERENCIA`, `EFECTIVO`.
3. **`model/enums/EstadoPedido.java`** — `PENDIENTE`, `CONFIRMADO`, `TERMINADO`, `CANCELADO`.
4. **`model/Calculable.java`** — interface con `void calcularTotal();`.
5. **`model/Base.java`** — `@MappedSuperclass`, `@SuperBuilder`, campos `id` (`@GeneratedValue(IDENTITY)`),
   `eliminado` (`nullable = false`, default `false`), `createdAt` (`updatable = false`, asignado en
   `@PrePersist` si es null).
6. **`model/Categoria.java`** — tabla `categorias`; `nombre` (único, `nullable = false`, 100),
   `descripcion` (500), `@OneToMany(mappedBy = "categoria")` `productos`, helper `addProducto`.
7. **`model/Producto.java`** — tabla `productos`; `nombre` (100), `precio`, `descripcion` (500),
   `stock`, `imagen`, `disponible` (default `TRUE`), `@ManyToOne(LAZY)` `categoria`
   (`@JoinColumn(name = "categoria_id")`).
8. **`model/Usuario.java`** — tabla `usuarios`; `nombre`, `apellido`, `mail` (único, 150),
   `celular`, `contrasena`, `rol` (default `USUARIO`), `@OneToMany(mappedBy = "usuario")` `pedidos`,
   helpers `addPedido` / `removePedido`.
9. **`model/DetallePedido.java`** — tabla `detalles_pedido`; `cantidad`, `subtotal`,
   `@ManyToOne(EAGER)` `producto` (`nullable = false`), `@ManyToOne(LAZY)` `pedido`,
   método `calcularSubtotal()`.
10. **`model/Pedido.java`** — tabla `pedidos`; `implements Calculable`; `fecha` (default `LocalDate.now()`,
    `updatable = false`), `estado` (default `PENDIENTE`), `total` (default `0.0`), `formaPago`,
    `@ManyToOne(LAZY)` `usuario`, `@OneToMany(mappedBy = "pedido", cascade = ALL, orphanRemoval = true)`
    `detalles`; métodos `addDetallePedido(int, Producto)`, `calcularTotal()`,
    `findDetallePedidoByProducto(Producto)`, `deleteDetallePedidoByProducto(Producto)`.

Respetar las anotaciones Lombok de tp1 (`@Getter`, `@Setter`, `@NoArgsConstructor`,
`@AllArgsConstructor`, `@SuperBuilder`, `@ToString(callSuper=true, exclude={...})` excluyendo
colecciones y el lado padre, `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` con
`@EqualsAndHashCode.Include` en el identificador de negocio: `Categoria.nombre`, `Producto.nombre`,
`Usuario.mail`, `DetallePedido.producto`).

### Criterios de aceptación

- Compila sin errores.
- Al arrancar con `spring.jpa.show-sql=true`, Hibernate crea las 5 tablas:
  `categorias`, `productos`, `usuarios`, `pedidos`, `detalles_pedido`.
- En `/h2-console` se ven las 5 tablas, cada una con las columnas `id`, `eliminado`, `created_at`.
- Existen las FK `productos.categoria_id`, `pedidos.usuario_id`, `detalles_pedido.producto_id`,
  `detalles_pedido.pedido_id`.
- Los constraints únicos de `categorias.nombre` y `usuarios.mail` figuran en el DDL generado.
- No hay ninguna referencia a `com.tp1springboot` en los imports.

---

## Fase 3 — Capa `repository`

**Objetivo:** portar los repositorios y agregar los derived queries que va a necesitar la API.

### Precondiciones

- Fase 2 completa (entidades mapeadas).

### Implementación

Crear `repository/` con 4 interfaces `@Repository extends JpaRepository<T, Long>`:

1. **`CategoriaRepository`**
   - `List<Categoria> findByEliminadoFalse()`
   - `Optional<Categoria> findByIdAndEliminadoFalse(Long id)`
   - `boolean existsByNombreIgnoreCase(String nombre)`

2. **`ProductoRepository`**
   - `List<Producto> findByEliminadoFalse()`
   - `Optional<Producto> findByIdAndEliminadoFalse(Long id)`
   - `List<Producto> findByCategoriaIdAndEliminadoFalse(Long categoriaId)`
   - `List<Producto> findByDisponibleTrueAndEliminadoFalse()`

3. **`UsuarioRepository`**
   - `List<Usuario> findByEliminadoFalse()`
   - `Optional<Usuario> findByIdAndEliminadoFalse(Long id)`
   - `Optional<Usuario> findByMailAndEliminadoFalse(String mail)` ← soporte del punto 8 de la consigna
   - `boolean existsByMail(String mail)`

4. **`PedidoRepository`**
   - `List<Pedido> findByEliminadoFalse()`
   - `Optional<Pedido> findByIdAndEliminadoFalse(Long id)`
   - `List<Pedido> findByUsuarioIdAndEliminadoFalse(Long usuarioId)`
   - `List<Pedido> findByEstadoAndEliminadoFalse(EstadoPedido estado)`
   - `@Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estado = :estado AND p.eliminado = false") Double totalFacturado(@Param("estado") EstadoPedido estado)`

Todas las consultas de lectura filtran por `eliminado = false` (regla de baja lógica).

### Criterios de aceptación

- Compila sin errores.
- La aplicación arranca: Spring Data resuelve los 4 repositorios como beans sin lanzar
  `PropertyReferenceException` (esto valida que todos los nombres de método derivan a propiedades reales).
- El log de arranque no muestra advertencias sobre queries no resolubles.
- No hay ninguna consulta JPQL escrita a mano salvo `totalFacturado`.

---

## Fase 4 — Capa `dto`

**Objetivo:** definir el contrato de entrada/salida de la API, con validaciones declarativas.

### Precondiciones

- Fase 2 completa (los DTOs referencian los enums de `model/enums`).
- Fase 0 completa (`spring-boot-starter-validation` en el classpath).

### Implementación

Crear `dto/<agregado>/` con `record`s. Diferencias respecto de tp1 (marcadas con **[adaptación]**):

**`dto/categoria/`**
- `CategoriaCreate(@NotBlank String nombre, String descripcion)`
- `CategoriaEdit(@NotBlank String nombre, String descripcion)` — **[adaptación]** sin `id`: en REST el id
  viaja por el path (`PUT /api/categorias/{id}`).
- `CategoriaPatch(String nombre, String descripcion)` — **[adaptación]** todos opcionales; solo se aplican
  los campos no nulos.
- `CategoriaDto(Long id, String nombre, String descripcion, int cantidadProductos)`

**`dto/producto/`**
- `ProductoCreate(@NotBlank String nombre, @NotNull @Positive Double precio, String descripcion, @NotNull @PositiveOrZero Integer stock, String imagen, Boolean disponible, @NotNull Long categoriaId)`
- `ProductoEdit(...)` — mismos campos y validaciones que `ProductoCreate`, **[adaptación]** sin `id`.
- `ProductoPatch(String nombre, @Positive Double precio, String descripcion, @PositiveOrZero Integer stock, String imagen, Boolean disponible, Long categoriaId)` — **[adaptación]**
- `ProductoStockPatch(@NotNull @PositiveOrZero Integer stock)` — **[adaptación]** para `PATCH /{id}/stock`.
- `ProductoDto(Long id, String nombre, Double precio, String descripcion, Integer stock, String imagen, Boolean disponible, Long categoriaId, String categoriaNombre)`

**`dto/usuario/`**
- `UsuarioCreate(@NotBlank String nombre, @NotBlank String apellido, @Email @NotBlank String mail, String celular, @NotBlank @Size(min = 6) String contrasena, Rol rol)`
- `UsuarioEdit(@NotBlank String nombre, @NotBlank String apellido, @Email @NotBlank String mail, String celular, Rol rol)` — **[adaptación]** sin `id`; sigue sin exponer contraseña.
- `UsuarioPatch(String nombre, String apellido, @Email String mail, String celular, Rol rol)` — **[adaptación]**
- `UsuarioDto(Long id, String nombre, String apellido, String mail, String celular, Rol rol, int cantidadPedidos)`
  — **nunca** incluye la contraseña.

**`dto/detallePedido/`**
- `DetallePedidoCreate(@NotNull Long productoId, @NotNull @Positive Integer cantidad)`
- `DetallePedidoDto(Long id, Long productoId, String productoNombre, Double precioUnitario, Integer cantidad, Double subtotal)`

**`dto/pedido/`**
- `PedidoCreate(@NotNull Long usuarioId, @NotNull FormaPago formaPago, @NotEmpty @Valid List<DetallePedidoCreate> detalles)`
  — **[adaptación]** en tp1 el alta viajaba dentro de `PedidoDto` (campo `detallesCreate`); acá se separa
  para que el schema de Swagger del request sea limpio y no muestre campos de salida.
- `PedidoEdit(EstadoPedido estado, FormaPago formaPago)` — **[adaptación]** sin `id`; ambos campos opcionales
  (si vienen en `null` no se modifican).
- `PedidoEstadoPatch(@NotNull EstadoPedido estado)` — **[adaptación]** para `PATCH /{id}/estado`.
- `PedidoDto(Long id, LocalDate fecha, EstadoPedido estado, Double total, FormaPago formaPago, Long usuarioId, String usuarioMail, List<DetallePedidoDto> detalles)`
  — **[adaptación]** solo salida; sin `detallesCreate`.

Anotar con `@Valid` las listas anidadas para que la validación se propague a cada `DetallePedidoCreate`.

### Criterios de aceptación

- Compila sin errores.
- Ningún DTO de salida (`*Dto`) expone la contraseña ni entidades JPA.
- Ningún DTO `*Edit` tiene campo `id`.
- Todos los DTOs de entrada tienen al menos una restricción `jakarta.validation` en sus campos obligatorios.
- `PedidoDto` no tiene ningún campo de entrada; `PedidoCreate` no tiene ningún campo derivado
  (`id`, `fecha`, `total`, `estado`).

---

## Fase 5 — Capa `mapper`

**Objetivo:** conversión explícita entidad ⇄ DTO, sin librerías de mapeo.

### Precondiciones

- Fases 2 y 4 completas.

### Implementación

Crear `mapper/` con 5 clases `@Component` (inyección por constructor donde haga falta):

1. **`CategoriaMapper`**
   - `Categoria toEntity(CategoriaCreate dto)`
   - `void updateEntity(Categoria entity, CategoriaEdit dto)`
   - `void patchEntity(Categoria entity, CategoriaPatch dto)` — **[adaptación]** aplica solo los campos no nulos.
   - `CategoriaDto toDto(Categoria entity)` — `cantidadProductos` = productos no eliminados de la categoría.
   - `List<CategoriaDto> toDtoList(List<Categoria> entities)`

2. **`ProductoMapper`**
   - `Producto toEntity(ProductoCreate dto, Categoria categoria)`
   - `void updateEntity(Producto entity, ProductoEdit dto, Categoria categoria)`
   - `void patchEntity(Producto entity, ProductoPatch dto, Categoria categoria)` — la categoría llega
     ya resuelta (o `null` si el patch no la trae).
   - `ProductoDto toDto(Producto entity)` — incluye `categoriaId` y `categoriaNombre`.
   - `List<ProductoDto> toDtoList(...)`

3. **`UsuarioMapper`**
   - `Usuario toEntity(UsuarioCreate dto)` — si `rol` es `null`, `Rol.USUARIO`.
   - `void updateEntity(Usuario entity, UsuarioEdit dto)` — no toca la contraseña.
   - `void patchEntity(Usuario entity, UsuarioPatch dto)`
   - `UsuarioDto toDto(Usuario entity)` — `cantidadPedidos` derivado de la colección.
   - `List<UsuarioDto> toDtoList(...)`

4. **`DetallePedidoMapper`**
   - `DetallePedidoDto toDto(DetallePedido entity)` — `productoNombre` y `precioUnitario` derivados
     del producto asociado.

5. **`PedidoMapper`** (recibe `DetallePedidoMapper` por constructor)
   - `PedidoDto toDto(Pedido entity)` — resuelve `usuarioId`/`usuarioMail` con guarda de `null`, y
     mapea cada detalle.
   - `List<PedidoDto> toDtoList(...)`

Los mappers **no** acceden a repositorios: las entidades relacionadas (`Categoria`, `Usuario`) las
resuelve el servicio y se las pasa como parámetro.

### Criterios de aceptación

- Compila sin errores.
- Los 5 mappers se registran como beans (la app arranca sin `NoSuchBeanDefinitionException`).
- Ningún mapper tiene dependencias de `repository/`.
- Los métodos `patchEntity` dejan intacto todo campo que llegue en `null`.
- `UsuarioMapper.toDto` no copia `contrasena` a ningún lado.

---

## Fase 6 — Capa `exception` (+ AdviceController)

**Objetivo:** manejo centralizado de errores con respuestas JSON consistentes — punto 4 de la consigna.

### Precondiciones

- Fase 0 completa (webmvc + validation).

### Implementación

Crear `exception/`:

1. **`RecursoNoEncontradoException extends RuntimeException`** — constructor `(String mensaje)`.
2. **`ReglaNegocioException extends RuntimeException`** — constructor `(String mensaje)`.
3. **`ErrorResponse`** — `record(LocalDateTime timestamp, int status, String error, String mensaje, String path, Map<String, String> errores)`.
4. **`GlobalExceptionHandler`** — `@RestControllerAdvice`, con un método privado
   `build(HttpStatus, String mensaje, HttpServletRequest, Map<String,String> errores)` y estos handlers:

   | Excepción | Status | Notas |
   |---|---|---|
   | `RecursoNoEncontradoException` | 404 | portado de tp1 |
   | `ReglaNegocioException` | 409 | portado de tp1 |
   | `MethodArgumentNotValidException` | 400 | portado de tp1; arma el mapa campo → mensaje desde `getFieldErrors()` |
   | `HandlerMethodValidationException` | 400 | **[nuevo]** validación de `@RequestParam`/`@PathVariable` |
   | `HttpMessageNotReadableException` | 400 | **[nuevo]** JSON malformado o enum inválido en el body |
   | `MethodArgumentTypeMismatchException` | 400 | **[nuevo]** ej. `/api/pedidos/abc` o `?estado=FOO` |
   | `DataIntegrityViolationException` | 409 | **[nuevo]** violación de unique a nivel BD |
   | `NoResourceFoundException` | 404 | **[nuevo]** ruta inexistente devuelta como JSON, no como página de error |
   | `Exception` | 500 | fallback, portado de tp1 |

### Criterios de aceptación

- Compila y la app arranca.
- `GET /api/ruta-que-no-existe` devuelve `404` con body JSON `ErrorResponse` (no HTML de Whitelabel).
- El JSON de error incluye siempre `timestamp`, `status`, `error`, `mensaje`, `path`; el campo
  `errores` solo aparece en los `400` de validación (queda omitido por `default-property-inclusion=non_null`).
- El handler genérico `Exception` está declarado último y no intercepta las excepciones específicas.

---

## Fase 7 — Capa `service`

**Objetivo:** portar toda la lógica de negocio de tp1, adaptada a las firmas que necesita REST.

### Precondiciones

- Fases 3, 4, 5 y 6 completas.

### Implementación

Crear `service/` con 4 clases `@Service`, inyección por constructor, `@Transactional(readOnly = true)`
en lecturas y `@Transactional` en escrituras.

1. **`CategoriaService`** (repos: `CategoriaRepository`; mapper: `CategoriaMapper`)
   - `CategoriaDto crear(CategoriaCreate dto)` — `ReglaNegocioException` si `existsByNombreIgnoreCase`.
   - `List<CategoriaDto> listar()`
   - `CategoriaDto buscarPorId(Long id)`
   - `CategoriaDto editar(Long id, CategoriaEdit dto)` — **[adaptación]** id por parámetro.
   - `CategoriaDto patch(Long id, CategoriaPatch dto)` — **[adaptación]**; si viene `nombre` distinto al
     actual y ya existe otra categoría con ese nombre → `ReglaNegocioException`.
   - `void eliminar(Long id)` — baja lógica.
   - `Categoria obtenerEntidad(Long id)` — uso interno, `RecursoNoEncontradoException` si no existe.

2. **`ProductoService`** (repos: `ProductoRepository`, `CategoriaRepository`; mapper: `ProductoMapper`)
   - `ProductoDto crear(ProductoCreate dto)` — resuelve categoría por id (404 si no existe).
   - `List<ProductoDto> listar()` / `listarPorCategoria(Long)` / `listarDisponibles()`
   - `ProductoDto buscarPorId(Long id)`
   - `ProductoDto editar(Long id, ProductoEdit dto)` — **[adaptación]**
   - `ProductoDto patch(Long id, ProductoPatch dto)` — **[adaptación]** resuelve categoría solo si
     `categoriaId` no es null.
   - `ProductoDto actualizarStock(Long id, Integer stock)` — **[adaptación]** para `PATCH /{id}/stock`.
   - `void eliminar(Long id)` — baja lógica.
   - `void descontarStock(Producto producto, int cantidad)` — `ReglaNegocioException` si stock insuficiente.
   - `Producto obtenerEntidad(Long id)`

3. **`UsuarioService`** (repo: `UsuarioRepository`; mapper: `UsuarioMapper`)
   - `UsuarioDto crear(UsuarioCreate dto)` — `ReglaNegocioException` si `existsByMail`.
   - `List<UsuarioDto> listar()`
   - `UsuarioDto buscarPorId(Long id)` — **además loguea por consola** los datos del usuario
     encontrado (punto 7 de la consigna).
   - `UsuarioDto buscarPorMail(String mail)` — **además loguea por consola** (punto 8 de la consigna).
     404 si no existe.
   - `UsuarioDto editar(Long id, UsuarioEdit dto)` — no toca la contraseña; si cambia el mail y ya
     existe otro usuario con ese mail → `ReglaNegocioException`.
   - `UsuarioDto patch(Long id, UsuarioPatch dto)` — **[adaptación]**, misma validación de mail.
   - `void eliminar(Long id)` — baja lógica.
   - `Usuario obtenerEntidad(Long id)`

   El logueo de los puntos 7 y 8 se hace con SLF4J (`private static final Logger log = LoggerFactory.getLogger(UsuarioService.class)`),
   en un método privado `logUsuario(String origen, Usuario usuario)` que imprime id, nombre,
   apellido, mail, celular, rol y cantidad de pedidos. Vive en el service (no en el controller) para
   que quede registrado se lo invoque desde donde se lo invoque.

4. **`PedidoService`** (repos: `PedidoRepository`, `UsuarioRepository`, `ProductoRepository`; mapper: `PedidoMapper`)
   - `PedidoDto crear(PedidoCreate dto)` — **una sola transacción**: resuelve usuario (404), valida que
     haya al menos un detalle (409), y por cada detalle resuelve producto (404), valida stock (409),
     hace `pedido.addDetallePedido(cantidad, producto)` y descuenta stock. Si falla el detalle N, se
     revierte todo, **incluido el stock ya descontado de los detalles anteriores**. Cierra con
     `pedido.calcularTotal()`.
   - `List<PedidoDto> listar()` / `listarPorUsuario(Long)` / `listarPorEstado(EstadoPedido)`
   - `PedidoDto buscarPorId(Long id)`
   - `PedidoDto editar(Long id, PedidoEdit dto)` — transiciones de estado guardadas: no se puede salir
     de `TERMINADO` ni de `CANCELADO` (409); pasar a `CANCELADO` desde `PENDIENTE`/`CONFIRMADO` repone
     el stock de todos los detalles. `formaPago` se actualiza solo si no es null.
   - `PedidoDto cambiarEstado(Long id, EstadoPedido estado)` — **[adaptación]** para `PATCH /{id}/estado`;
     reutiliza la misma guarda de transiciones.
   - `PedidoDto agregarDetalle(Long pedidoId, DetallePedidoCreate dto)` — solo si el pedido está
     `PENDIENTE` (409 en otro caso); valida y descuenta stock.
   - `PedidoDto quitarDetalle(Long pedidoId, Long productoId)` — 404 si el pedido no tiene ese producto;
     devuelve el stock y recalcula el total.
   - `void eliminar(Long id)` — baja lógica.
   - `Double totalFacturado()` — suma de los pedidos `TERMINADO`.
   - `Pedido obtenerEntidad(Long id)`

Con `spring.jpa.open-in-view=false`, **todo el mapeo a DTO ocurre dentro del método transaccional del
service** (los controllers reciben DTOs ya materializados; nunca disparan lazy loading).

### Criterios de aceptación

- Compila y la app arranca con los 4 services como beans.
- Ningún service devuelve entidades: todas las firmas públicas devuelven DTOs o tipos primitivos,
  salvo los `obtenerEntidad(...)` de uso interno.
- Ningún service usa `@Autowired` ni inyección por campo.
- `PedidoService.crear` está anotado `@Transactional` (no `readOnly`) y no hace `save` parcial fuera
  de esa transacción.
- Todo método de borrado setea `eliminado = true` y guarda; no existe ninguna llamada a
  `repository.delete(...)` o `deleteById(...)` en el código.
- `UsuarioService.buscarPorId` y `buscarPorMail` emiten una línea de log con los datos del usuario.

---

## Fase 8 — `CategoriaController`

**Objetivo:** primer controller REST completo; fija el patrón que siguen los tres restantes.

### Precondiciones

- Fases 6 y 7 completas.

### Implementación

Crear `controller/CategoriaController.java`:

- `@RestController`, `@RequestMapping("/api/categorias")`, inyección por constructor de `CategoriaService`.
- `@Tag(name = "Categorías", description = "ABM de categorías de productos")`.
- Endpoints:

  | Método | Ruta | Body | Respuesta |
  |---|---|---|---|
  | `GET` | `` | — | `200` `List<CategoriaDto>` |
  | `GET` | `/{id}` | — | `200` `CategoriaDto` |
  | `POST` | `` | `@Valid CategoriaCreate` | `201` + header `Location: /api/categorias/{id}` + `CategoriaDto` |
  | `PUT` | `/{id}` | `@Valid CategoriaEdit` | `200` `CategoriaDto` |
  | `PATCH` | `/{id}` | `CategoriaPatch` | `200` `CategoriaDto` |
  | `DELETE` | `/{id}` | — | `204` sin body |

- El `201` se arma con `ResponseEntity.created(URI.create("/api/categorias/" + dto.id())).body(dto)`.
- Documentación springdoc por endpoint: `@Operation(summary = ..., description = ...)` y
  `@ApiResponses` con los códigos `200/201/204/400/404/409` según corresponda.
- El controller **no** contiene lógica de negocio: solo delega en el service y arma el `ResponseEntity`.

### Criterios de aceptación

- `POST /api/categorias` con `{"nombre":"Electrónica","descripcion":"..."}` devuelve `201`, header
  `Location` correcto y el `CategoriaDto` con `id` asignado.
- `POST` repitiendo el mismo nombre devuelve `409` con el `ErrorResponse` de `ReglaNegocioException`.
- `POST` con `{"nombre":""}` devuelve `400` con `errores: {"nombre": "..."}`.
- `GET /api/categorias/{id}` de un id inexistente devuelve `404`.
- `PUT /api/categorias/{id}` actualiza nombre y descripción y devuelve `200` con los valores nuevos.
- `PATCH /api/categorias/{id}` con `{"descripcion":"nueva"}` cambia solo la descripción y deja el
  nombre intacto.
- `DELETE /api/categorias/{id}` devuelve `204`; el siguiente `GET /api/categorias` ya no la lista,
  pero en `/h2-console` la fila sigue existiendo con `eliminado = TRUE`.
- Los 6 endpoints aparecen en Swagger UI bajo el tag "Categorías".

---

## Fase 9 — `ProductoController`

### Precondiciones

- Fase 8 completa (patrón de controller establecido).

### Implementación

Crear `controller/ProductoController.java` — `@RestController`, `@RequestMapping("/api/productos")`,
`@Tag(name = "Productos", ...)`:

| Método | Ruta | Body / Params | Respuesta |
|---|---|---|---|
| `GET` | `` | `?categoriaId=` (opt), `?disponible=` (opt) | `200` `List<ProductoDto>` |
| `GET` | `/{id}` | — | `200` `ProductoDto` |
| `POST` | `` | `@Valid ProductoCreate` | `201` + `Location` |
| `PUT` | `/{id}` | `@Valid ProductoEdit` | `200` |
| `PATCH` | `/{id}` | `ProductoPatch` | `200` |
| `PATCH` | `/{id}/stock` | `@Valid ProductoStockPatch` | `200` |
| `DELETE` | `/{id}` | — | `204` |

En el `GET` de listado, resolver los query params en este orden: si viene `categoriaId` →
`listarPorCategoria`; si viene `disponible=true` → `listarDisponibles`; si no → `listar`.
Documentar los params con `@Parameter(description = ...)`.

### Criterios de aceptación

- `POST /api/productos` con `categoriaId` válido devuelve `201` y el `ProductoDto` incluye
  `categoriaNombre` resuelto.
- `POST /api/productos` con `categoriaId` inexistente devuelve `404`.
- `POST` con `precio: -5` o `stock: -1` devuelve `400` con el campo señalado en `errores`.
- `GET /api/productos?categoriaId={id}` devuelve solo los productos de esa categoría.
- `PATCH /api/productos/{id}` con `{"precio": 999.99}` cambia solo el precio.
- `PATCH /api/productos/{id}/stock` con `{"stock": 50}` deja el stock en 50 exacto.
- `DELETE /api/productos/{id}` devuelve `204` y el producto desaparece de los listados.
- Los 7 endpoints figuran en Swagger UI bajo "Productos".

---

## Fase 10 — `UsuarioController` (cubre los puntos 7 y 8 de la consigna)

### Precondiciones

- Fase 8 completa.
- Fase 7 completa, con el logueo por consola ya implementado en `UsuarioService`.

### Implementación

Crear `controller/UsuarioController.java` — `@RestController`, `@RequestMapping("/api/usuarios")`,
`@Tag(name = "Usuarios", ...)`:

| Método | Ruta | Body / Params | Respuesta |
|---|---|---|---|
| `GET` | `` | — | `200` `List<UsuarioDto>` |
| `GET` | `/{id}` | — | `200` `UsuarioDto` — **punto 7**: además imprime por consola |
| `GET` | `/buscar` | `?mail=` (`@NotBlank`) | `200` `UsuarioDto` — **punto 8**: además imprime por consola |
| `GET` | `/{id}/pedidos` | — | `200` `List<PedidoDto>` (delega en `PedidoService.listarPorUsuario`) |
| `POST` | `` | `@Valid UsuarioCreate` | `201` + `Location` |
| `PUT` | `/{id}` | `@Valid UsuarioEdit` | `200` |
| `PATCH` | `/{id}` | `UsuarioPatch` | `200` |
| `DELETE` | `/{id}` | — | `204` |

- `GET /{id}/pedidos` requiere inyectar también `PedidoService` en el controller (dos services, ambos
  por constructor).
- Documentar explícitamente en el `@Operation` de `/{id}` y de `/buscar` que la operación **también
  imprime la información del usuario por consola**, para que la consigna quede trazable desde Swagger.

### Criterios de aceptación

- `POST /api/usuarios` devuelve `201` y el `UsuarioDto` **no** contiene el campo `contrasena`.
- `POST` con un mail ya existente devuelve `409`.
- `POST` con `mail: "no-es-mail"` o `contrasena` de menos de 6 caracteres devuelve `400`.
- `GET /api/usuarios/{id}` devuelve `200` **y** en la consola de la aplicación aparece una línea con
  id, nombre, apellido, mail, celular, rol y cantidad de pedidos (punto 7).
- `GET /api/usuarios/buscar?mail=...` devuelve `200` **y** imprime la misma información por consola
  (punto 8); con un mail inexistente devuelve `404`.
- Los acentos y la `ñ` se ven correctamente en esa salida de consola en Windows (valida el UTF-8 de la Fase 0).
- `GET /api/usuarios/{id}/pedidos` devuelve la lista de pedidos de ese usuario.
- Los 8 endpoints figuran en Swagger UI bajo "Usuarios".

---

## Fase 11 — `PedidoController`

**Objetivo:** exponer el agregado más complejo (alta transaccional con detalles, estados, stock).

### Precondiciones

- Fases 8 y 9 completas (necesita productos cargados para probar).
- Fase 10 completa (necesita usuarios cargados).

### Implementación

Crear `controller/PedidoController.java` — `@RestController`, `@RequestMapping("/api/pedidos")`,
`@Tag(name = "Pedidos", ...)`:

| Método | Ruta | Body / Params | Respuesta |
|---|---|---|---|
| `GET` | `` | `?usuarioId=` (opt), `?estado=` (opt) | `200` `List<PedidoDto>` |
| `GET` | `/{id}` | — | `200` `PedidoDto` |
| `GET` | `/total-facturado` | — | `200` `Double` (suma de pedidos `TERMINADO`) |
| `POST` | `` | `@Valid PedidoCreate` | `201` + `Location` |
| `PUT` | `/{id}` | `PedidoEdit` | `200` |
| `PATCH` | `/{id}/estado` | `@Valid PedidoEstadoPatch` | `200` |
| `POST` | `/{id}/detalles` | `@Valid DetallePedidoCreate` | `200` `PedidoDto` actualizado |
| `DELETE` | `/{id}/detalles/{productoId}` | — | `200` `PedidoDto` actualizado |
| `DELETE` | `/{id}` | — | `204` |

- Ojo con el orden de mapeo: declarar `/total-facturado` **antes** que `/{id}` no es necesario en
  Spring MVC (las rutas literales ganan sobre las variables), pero sí verificar que
  `GET /api/pedidos/total-facturado` no caiga en el handler de `/{id}` devolviendo `400`.
- `DELETE /{id}/detalles/{productoId}` devuelve `200` con el pedido resultante (no `204`), porque el
  recurso pedido sigue existiendo y el cliente necesita el total recalculado.

### Criterios de aceptación

- `POST /api/pedidos` con 2 detalles válidos devuelve `201`; el `PedidoDto` trae `estado: "PENDIENTE"`,
  `fecha` de hoy, `total` = suma de subtotales, `usuarioMail` resuelto y los 2 detalles con
  `productoNombre`, `precioUnitario` y `subtotal`.
- Tras ese alta, el stock de cada producto involucrado bajó exactamente por la cantidad pedida.
- `POST /api/pedidos` con `detalles: []` devuelve `400` (por `@NotEmpty`).
- `POST /api/pedidos` donde el **segundo** detalle excede el stock devuelve `409` y **el stock del
  primer producto queda intacto** (rollback de la transacción completa) — verificable en `/h2-console`.
- `POST /api/pedidos` con `usuarioId` o `productoId` inexistente devuelve `404`.
- `PATCH /api/pedidos/{id}/estado` con `{"estado":"CANCELADO"}` sobre un pedido `PENDIENTE` devuelve
  `200` y repone el stock de todos sus detalles.
- Volver a cambiar el estado de un pedido `CANCELADO` o `TERMINADO` devuelve `409`.
- `POST /api/pedidos/{id}/detalles` sobre un pedido no `PENDIENTE` devuelve `409`.
- `DELETE /api/pedidos/{id}/detalles/{productoId}` devuelve el pedido con el total recalculado y
  repone el stock de ese producto.
- `GET /api/pedidos/total-facturado` devuelve `0.0` si no hay pedidos `TERMINADO`, y la suma correcta
  cuando los hay.
- Los 9 endpoints figuran en Swagger UI bajo "Pedidos".

---

## Fase 12 — Documentación Swagger completa (punto 9 de la consigna)

**Objetivo:** que Swagger UI sirva como documentación y como banco de pruebas de toda la API.

### Precondiciones

- Fases 8 a 11 completas (los 4 controllers existen).
- Fase 1 resuelta (Swagger accesible).

### Implementación

1. **`config/OpenApiConfig`** — completar el bean `OpenAPI`: `info` (título, versión, descripción,
   `contact` con nombre y mail del autor, `license`), y la lista de `tags` con descripción de los 4
   grupos (Categorías, Productos, Usuarios, Pedidos).

2. **Anotaciones en los DTOs** — agregar `@Schema(description = ..., example = ...)` en los campos de
   los `record`s de entrada (`*Create`, `*Edit`, `*Patch`, `DetallePedidoCreate`), para que Swagger
   muestre ejemplos utilizables con un clic. Como mínimo, ejemplos en:
   `CategoriaCreate.nombre`, `ProductoCreate` (todos los campos), `UsuarioCreate` (todos),
   `PedidoCreate` (`usuarioId`, `formaPago`, `detalles`).

3. **`@ApiResponses` uniformes** — revisar que cada endpoint declare los códigos que realmente puede
   devolver, con `content = @Content(schema = @Schema(implementation = ErrorResponse.class))` en los
   `400/404/409`.

4. **`ErrorResponse`** — anotar con `@Schema(description = "Respuesta de error estándar de la API")`.

5. Verificar en la UI que los enums (`Rol`, `FormaPago`, `EstadoPedido`) se rendericen como
   desplegables con sus valores.

### Criterios de aceptación

- `http://localhost:8080/swagger-ui.html` muestra los 4 tags con sus endpoints agrupados
  (30 operaciones en total: 6 + 7 + 8 + 9).
- Cada operación tiene `summary` legible en español.
- El botón "Try it out" funciona: se puede crear una categoría desde Swagger UI y recibe `201`.
- Los schemas de request muestran valores de ejemplo precargados.
- Los códigos `400`, `404` y `409` documentan el schema `ErrorResponse`.
- Los campos de tipo enum se muestran como lista de valores permitidos.
- `GET /v3/api-docs` devuelve un documento OpenAPI que Postman puede importar sin errores.

---

## Fase 13 — Carga de datos por Postman (puntos 5, 6, 7 y 8 de la consigna)

**Objetivo:** ejecutar la secuencia exacta que pide el enunciado contra la API ya terminada, y dejarla
guardada como colección reproducible.

### Precondiciones

- Fases 8 a 12 completas: la API responde y está documentada.
- La app corre en `http://localhost:8080` con `app.seed.enabled=false` (base vacía al arrancar).
- Postman instalado.

### Implementación

1. Crear una colección Postman `TP-API-REST-Pedidos` con una variable de entorno
   `baseUrl = http://localhost:8080/api`, y carpetas por recurso.

2. **Punto 5c — 3 categorías**: `POST {{baseUrl}}/categorias` ×3
   (ej.: "Electrónica", "Indumentaria", "Hogar"). Guardar los ids devueltos en variables de colección
   (`categoriaElectronicaId`, etc.) con un script de test que solo haga `pm.collectionVariables.set(...)`.

3. **Punto 5d — 10 productos**: `POST {{baseUrl}}/productos` ×10, repartidos entre las 3 categorías,
   todos con `stock` suficiente para los pedidos posteriores. Guardar los ids.

4. **Punto 5a — 2 usuarios**: `POST {{baseUrl}}/usuarios` ×2 (uno con `rol: "ADMIN"`, otro con
   `rol: "USUARIO"`). Guardar ids y mails.

5. **Punto 5b — 3 pedidos con ≥2 detalles cada uno**: `POST {{baseUrl}}/pedidos` ×3, con
   `usuarioId` de los usuarios creados, `formaPago` variando entre `TARJETA`, `TRANSFERENCIA` y
   `EFECTIVO`, y `detalles` con 2 o 3 líneas cada uno.

6. **Punto 6 — actualizar 1 categoría**: `PUT {{baseUrl}}/categorias/{{categoriaElectronicaId}}`
   con nombre y descripción nuevos. Agregar además un `PATCH` sobre la misma categoría para dejar
   demostrado el método parcial que pide el marco teórico del PDF.

7. **Punto 7 — buscar usuario por id y mostrar por consola**:
   `GET {{baseUrl}}/usuarios/{{usuarioAdminId}}` y capturar la salida de la consola de la aplicación.

8. **Punto 8 — buscar usuario por mail y mostrar por consola**:
   `GET {{baseUrl}}/usuarios/buscar?mail={{usuarioAdminMail}}` y capturar la salida de la consola.

9. Agregar al final una carpeta "Casos de error" con: alta duplicada (`409`), alta inválida (`400`),
   búsqueda inexistente (`404`) y pedido con stock insuficiente (`409`).

10. Exportar la colección (y el environment) a `tp2-spring-boot/postman/` como
    `TP-API-REST-Pedidos.postman_collection.json`.

### Criterios de aceptación

- Ejecutando la colección completa de arriba a abajo sobre una base recién arrancada, **todos** los
  requests de las carpetas 2–8 devuelven `2xx`.
- Al terminar existen en la base: 3 categorías, 10 productos, 2 usuarios, 3 pedidos y ≥6 detalles
  de pedido (verificable en `/h2-console` o con `GET /api/pedidos`).
- Cada uno de los 3 pedidos devuelve al menos 2 elementos en `detalles` y un `total` distinto de 0.
- El stock de los productos usados en pedidos quedó descontado respecto del valor de alta.
- El `PUT` de la categoría devuelve `200` con el nombre y la descripción nuevos, y un `GET` posterior
  los confirma.
- La consola de la aplicación muestra la información del usuario en las búsquedas por id y por mail.
- Los 4 requests de la carpeta "Casos de error" devuelven exactamente los códigos esperados
  (`409`, `400`, `404`, `409`) con body `ErrorResponse`.
- La colección exportada está versionada en `tp2-spring-boot/postman/`.

---

## Fase 14 — Seeder opcional y documentación final del proyecto

**Objetivo:** dejar el proyecto entregable y reproducible sin depender de correr Postman a mano.

### Precondiciones

- Fase 13 completa (el juego de datos de la consigna quedó definido).

### Implementación

1. **`config/DataSeeder`** — portar el seeder de tp1 como `@Component` + `CommandLineRunner`,
   anotado con `@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")` —
   **sin `matchIfMissing = true`**, de modo que por defecto quede apagado y no interfiera con la carga
   por Postman que pide la consigna. Debe cargar exactamente el mismo juego de datos de la Fase 13
   (3 categorías, 10 productos, 2 usuarios, 3 pedidos con ≥2 detalles) instanciando **siempre a través
   de los services y sus DTOs**, nunca con `new Categoria(...)`, para que también ejercite las validaciones.

2. **`README.md`** del proyecto, con:
   - Descripción y stack (versión de Spring Boot y de springdoc efectivamente usadas, según lo resuelto
     en la Fase 1).
   - Cómo levantar: `./gradlew bootRun` (o `gradlew.bat bootRun` en Windows) y `./gradlew bootJar`.
   - URLs: Swagger UI, `/v3/api-docs`, `/h2-console` (con JDBC URL, usuario y password).
   - Tabla completa de endpoints (la de la sección "Contrato REST" de este plan).
   - Cómo activar el seeder (`app.seed.enabled=true`).
   - Mapeo explícito **punto de la consigna → cómo se cumple**, con el request o la captura que lo
     evidencia (los 9 puntos del PDF).
   - Diferencias documentadas respecto de `tp1springboot` (sin menú de consola, DTOs `*Edit` sin `id`,
     `PedidoCreate` separado de `PedidoDto`, PATCH agregado).

3. **`docs/` o `evidencias/`** — capturas de: Swagger UI con los 4 tags desplegados, un `POST` exitoso
   desde Postman, la consola mostrando la búsqueda de usuario por id y por mail, y `/h2-console` con
   los datos cargados.

### Criterios de aceptación

- Arrancando con la configuración por defecto (`app.seed.enabled=false`), la base queda vacía y no se
  loguea nada del seeder.
- Arrancando con `--app.seed.enabled=true`, se cargan 3 categorías, 10 productos, 2 usuarios y
  3 pedidos, y el log de resumen lo confirma.
- Con el seeder activo, la aplicación arranca sin excepciones y `GET /api/pedidos` devuelve 3 pedidos
  con sus detalles.
- El `README.md` permite a alguien que clona el repo levantar el proyecto y abrir Swagger sin ayuda extra.
- Cada uno de los 9 puntos del PDF tiene, en el `README.md`, la fila que indica dónde se demuestra.
- No queda en el proyecto ningún archivo del paquete `console/` ni referencia a
  `app.console.enabled`.

---

## Anexo — Checklist de cierre contra la consigna

| # | Consigna | Fase que lo cubre | Evidencia |
|---|---|---|---|
| 1 | Dependencia de Swagger | Fase 1 | `build.gradle` + Swagger UI cargando |
| 2 | Capa Repository | Fase 3 | 4 interfaces `@Repository` |
| 3 | Capa Service | Fase 7 | 4 `@Service` con `@Transactional` |
| 4 | Capa Controller + AdviceController | Fases 6, 8–11 | 4 `@RestController` + `GlobalExceptionHandler` |
| 5a | 2 usuarios vía DTO/Postman | Fase 13 | `POST /api/usuarios` ×2 |
| 5b | 3 pedidos con ≥2 detalles | Fase 13 | `POST /api/pedidos` ×3 |
| 5c | 3 categorías | Fase 13 | `POST /api/categorias` ×3 |
| 5d | 10 productos | Fase 13 | `POST /api/productos` ×10 |
| 6 | Actualizar 1 categoría | Fase 13 | `PUT /api/categorias/{id}` (+ `PATCH`) |
| 7 | Buscar usuario por id y mostrar por consola | Fases 7, 10, 13 | `GET /api/usuarios/{id}` + log |
| 8 | Buscar usuario por mail y mostrar por consola | Fases 7, 10, 13 | `GET /api/usuarios/buscar?mail=` + log |
| 9 | Swagger funcionando | Fases 1, 12 | Captura de Swagger UI con 30 operaciones |
| — | Todos los métodos HTTP (GET/POST/PUT/PATCH/DELETE) | Fases 8–11 | Tabla de contrato REST |
