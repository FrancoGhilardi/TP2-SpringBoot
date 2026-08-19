# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`tp2-spring-boot` is a university (UTN, Programación IV) assignment: turn the console-based order-management domain from the sibling project `tp1springboot` (see `../tp1springboot`, its own git repo) into a REST API with Swagger/OpenAPI documentation. The assignment brief is `TP1_Api_Rest_Programacion_IV.pdf`.

As of now this is still the raw `start.spring.io` skeleton (`Tp2SpringBootApplication` + one generated test) — no domain code has been written yet. **`PLAN_IMPLEMENTACION.md` is the source of truth for how to build it**: it lays out 14 ordered phases (0–14, each leaves the project compiling and running), the full REST contract, DTO/entity field lists, and phase-by-phase acceptance criteria. Read it before adding code here — don't re-derive the plan from scratch.

Root package: `com.tp2springboot.tp2_spring_boot` (underscore — `com.tp2springboot.tp2-spring-boot` isn't a legal Java package name, per `HELP.md`). All new code goes under `src/main/java/com/tp2springboot/tp2_spring_boot/`.

## Commands

Run from this directory (`tp2-spring-boot/`):

```bash
./gradlew bootRun          # start the app (port 8080)
./gradlew test             # run all tests (JUnit 5 / useJUnitPlatform)
./gradlew build             # compile + test + assemble
./gradlew dependencies --configuration runtimeClasspath   # verify a dependency landed on the classpath
```

On Windows outside a bash-compatible shell, use `gradlew.bat` instead of `./gradlew`.

## Key decisions from the plan (don't relitigate these)

- **Model is a 1:1 port of `tp1springboot`'s domain** (`Base`, `Calculable`, `Categoria`, `Producto`, `Usuario`, `Pedido`, `DetallePedido`, enums `Rol`/`FormaPago`/`EstadoPedido`) — same fields, same relationships, no domain changes. Copy from `tp1springboot/src/main/java/com/tp1springboot/tp1springboot/model/`, updating only package/imports.
- **Conventions carried over from tp1** (see the parent `CLAUDE.md` one level up for the full rationale): constructor injection only, soft deletes (`eliminado` flag, never physical delete), bidirectional JPA relations with FK on the child side, DTOs as records under `dto/<agregado>/`, manual `@Component` mappers (no MapStruct), `RecursoNoEncontradoException`/`ReglaNegocioException` handled centrally, services as the transactional boundary with `spring.jpa.open-in-view=false` (controllers only ever see DTOs, never trigger lazy loading).
- **What's different from tp1 here** (this is a REST API, not a console app):
  - No `console/` package migrated. Assignment points 7/8 ("search user by id/mail and show in console") are satisfied by `UsuarioService` logging via SLF4J when `buscarPorId`/`buscarPorMail` are called — not by an interactive menu.
  - `*Edit` DTOs drop `id` (it travels in the path for `PUT`/`PATCH`); `*Patch` DTOs are new, all-optional-fields variants for partial updates.
  - `PedidoCreate` is a separate DTO from `PedidoDto` (tp1 conflated them via a `detallesCreate` field); here `PedidoDto` is output-only.
  - `PATCH` endpoints are new throughout (tp1 has no PATCH).
  - `config/DataSeeder` is ported but **disabled by default** (`app.seed.enabled=false`) — the assignment requires creating the initial data set (2 usuarios, 3 pedidos, 3 categorías, 10 productos) via Postman against the live API, not a seeder.
- **Swagger/OpenAPI risk called out in the plan**: the assignment specifies `springdoc-openapi-starter-webmvc-ui:2.5.0`, but this project runs Spring Boot 4.1.0 (Spring Framework 7), which that version wasn't built against. Plan's Fase 1 tries `springdoc 2.8.6` (latest 2.x) first; if that doesn't boot cleanly, the documented fallback is downgrading the Spring Boot plugin to `3.5.x` and reverting Boot-4-specific starter names (`spring-boot-starter-webmvc` → `spring-boot-starter-web`, drop `spring-boot-h2console`, etc.). Whichever path is taken must get recorded (originally slated for the `README.md` in Fase 13/14, not yet created).
- `application.properties` currently only has `spring.application.name` — Fase 0 of the plan specifies the full H2/JPA/logging/seed configuration to add (H2 at `jdbc:h2:mem:pedidosdb`, `ddl-auto=update`, `spring.jackson.default-property-inclusion=non_null`, etc.).

## Testing

Only the generated `Tp2SpringBootApplicationTests` (context-load smoke test) exists. No per-service/per-repository suite yet.
