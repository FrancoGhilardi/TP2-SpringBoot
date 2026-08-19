# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`tp2-spring-boot` is a REST API for an order-management system ("Sistema de Gestión de Pedidos") — categories, products, users and orders — built with Spring Boot and documented with Swagger/OpenAPI. University assignment (UTN, Programación IV).

Root package: `com.tp2springboot.tp2_spring_boot` (underscore — `com.tp2springboot.tp2-spring-boot` isn't a legal Java package name).

## Commands

Run from this directory (`tp2-spring-boot/`):

```bash
./gradlew bootRun          # start the app (port 8080)
./gradlew bootJar && java -jar build/libs/tp2-spring-boot-0.0.1-SNAPSHOT.jar
./gradlew test              # run all tests (JUnit 5 / useJUnitPlatform)
./gradlew build              # compile + test + assemble
./gradlew dependencies --configuration runtimeClasspath   # verify a dependency landed on the classpath
```

On Windows outside a bash-compatible shell, use `gradlew.bat` instead of `./gradlew`.

By default the app starts with an empty database (`app.seed.enabled=false`); pass `--app.seed.enabled=true` to load demo data (3 categorías, 10 productos, 2 usuarios, 3 pedidos) via `config/DataSeeder`.

## Architecture

Stack: Spring Boot 4.1.0 (Spring Framework 7), Java 21 (toolchain), Gradle 9.5.1, Spring Data JPA, Spring Web MVC, Spring Validation, H2 in-memory, Lombok, DevTools, springdoc-openapi 2.8.6.

Package structure under `com.tp2springboot.tp2_spring_boot`:

```
model            JPA entities (@Entity) + enums + Base/Calculable
  enums          EstadoPedido, FormaPago, Rol
dto              input/output records grouped by aggregate (categoria, producto, usuario, pedido, detallePedido)
mapper           manual entity <-> DTO conversion (@Component, no MapStruct/ModelMapper)
repository       Spring Data JPA interfaces (@Repository)
service          business logic (@Service, @Transactional)
controller       REST controllers (@RestController), springdoc-annotated
exception        domain exceptions + @RestControllerAdvice global handler
config           OpenApiConfig, DataSeeder (@Component, CommandLineRunner)
```

Key conventions to follow when extending this project:

- **Constructor injection only** — every `@Service`/`@Component`/`@RestController` takes `final` fields via a constructor; no `@Autowired`, no field injection.
- **Soft deletes everywhere.** All entities extend `Base` (`@MappedSuperclass`), which provides `id`, `eliminado` (boolean, defaults false) and `createdAt` (set in `@PrePersist`). Deleting a record means setting `eliminado = true` and saving — never a physical delete (`repository.delete(...)`/`deleteById(...)` are never called). Repository queries that should exclude soft-deleted rows use derived-query methods like `findByIdAndEliminadoFalse`, `findByEliminadoFalse`.
- **Bidirectional JPA relations own the FK on the child side** (`@ManyToOne` on `Producto.categoria`, `DetallePedido.pedido`, `Pedido.usuario`), with the parent side using `mappedBy`. This lets Spring Data generate derived queries from method names alone, without hand-written JPQL (the one exception is `PedidoRepository.totalFacturado`). Entity helper methods like `Pedido.addDetallePedido(...)` / `Usuario.addPedido(...)` keep both sides of the relation in sync.
- **DTOs are Java `record`s**, one set per aggregate under `dto/<aggregate>/`: `*Create` (POST body), `*Edit` (PUT body, no `id` — it travels in the path), `*Patch` (PATCH body, all fields optional, only non-null fields are applied), and `*Dto` (response, output-only). `PedidoCreate` is separate from `PedidoDto` so the request/response Swagger schemas stay clean.
- **Mappers are manual `@Component` classes**, not MapStruct — keep conversions explicit and in `mapper/`. They never depend on `repository/`; related entities (`Categoria`, `Usuario`) are resolved by the service and passed in as parameters. `patchEntity(...)` methods only touch non-null fields.
- **Exceptions**: throw `RecursoNoEncontradoException` for missing entities (404), `ReglaNegocioException` for business-rule violations (409, e.g. insufficient stock, duplicate mail/name, invalid state transition). Both — plus validation, malformed JSON, type-mismatch, DB-integrity, and unknown-route errors — are handled centrally by `GlobalExceptionHandler` (`@RestControllerAdvice`) and turned into a uniform `ErrorResponse` (`timestamp`, `status`, `error`, `mensaje`, `path`, `errores`).
- **Services are transactional boundaries.** Reads use `@Transactional(readOnly = true)`; writes use `@Transactional`. Multi-step operations (e.g. `PedidoService.crear` creating a pedido plus all its detalles and decrementing stock) run inside a single `@Transactional` method so a mid-loop failure (e.g. insufficient stock on the 3rd item) rolls back everything already applied, including stock already decremented for earlier items in the same call. With `spring.jpa.open-in-view=false`, all entity→DTO mapping happens inside the transactional service method — controllers only ever see DTOs, never trigger lazy loading.
- **`EstadoPedido` transitions are guarded** (in `PedidoService`, via a shared private helper reused by `editar` and `cambiarEstado`): you cannot leave `TERMINADO` or `CANCELADO` once reached; cancelling a `PENDIENTE`/`CONFIRMADO` pedido restores stock for all its detalles.
- **`UsuarioService.buscarPorId`/`buscarPorMail` log the found user via SLF4J** (id, nombre, apellido, mail, celular, rol, cantidad de pedidos) — this is how the assignment's "look up a user and show it on the console" requirements are satisfied in a REST-only project (no interactive console menu).
- Controllers are thin: no business logic, just delegate to the service and build the `ResponseEntity` (`201` + `Location` on create, `204` on delete, `200` otherwise). Each is `@Tag`-annotated for Swagger grouping and documents its `@ApiResponses` (400/404/409 reference the `ErrorResponse` schema).
- `config/DataSeeder` (`@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")`, no `matchIfMissing`, so off by default) always instantiates data through the services and their DTOs — never `new Categoria(...)` — so it also exercises the real validation path.

## Testing

Only the generated `Tp2SpringBootApplicationTests` (context-load smoke test) exists. No per-service/per-repository test suite yet.
