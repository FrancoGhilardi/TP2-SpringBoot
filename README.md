# TP API REST — Sistema de Gestión de Pedidos

API REST para un sistema de gestión de pedidos (categorías, productos, usuarios y pedidos),
desarrollada como Trabajo Práctico de Programación IV (UTN), documentada con Swagger/OpenAPI.

## Stack

- Spring Boot **4.1.0** (Spring Framework 7), Java 21 (toolchain), Gradle 9.5.1.
- Spring Data JPA, Spring Web MVC, Spring Validation, H2 en memoria, Lombok, DevTools.
- **springdoc-openapi-starter-webmvc-ui 2.8.6** para Swagger/OpenAPI.

## Cómo levantar el proyecto

Desde `tp2-spring-boot/`:

```bash
./gradlew bootRun          # levanta la app en :8080 (gradlew.bat en Windows fuera de bash)
./gradlew bootJar && java -jar build/libs/tp2-spring-boot-0.0.1-SNAPSHOT.jar
./gradlew test             # tests
```

Por defecto la base arranca **vacía** (`app.seed.enabled=false`): la consigna pide crear el juego
de datos inicial a través de Postman contra la API ya viva (ver sección "Seeder" más abajo para
cargarlo automáticamente en su lugar).

## URLs

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Consola H2 | http://localhost:8080/h2-console |

Credenciales de la consola H2: JDBC URL `jdbc:h2:mem:pedidosdb`, usuario `sa`, contraseña vacía.

## Contrato REST

Base path: `/api`. Baja lógica en todos los `DELETE` (nunca se borra físicamente).

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

Errores uniformes vía `ErrorResponse` (`timestamp`, `status`, `error`, `mensaje`, `path`, y
`errores` solo en validaciones): `400` validación/parámetro inválido/JSON malformado, `404`
recurso inexistente, `409` violación de regla de negocio, `500` fallo no controlado.

## Seeder (carga automática opcional)

La consigna pide crear el juego de datos inicial desde Postman, así que el seeder está
**apagado por defecto**. Para levantar la app con datos de ejemplo ya cargados (3 categorías, 10
productos, 2 usuarios, 3 pedidos con ≥2 detalles cada uno) sin usar Postman:

```bash
./gradlew bootRun --args='--app.seed.enabled=true'
# o, corriendo el jar:
java -jar build/libs/tp2-spring-boot-0.0.1-SNAPSHOT.jar --app.seed.enabled=true
```

El seeder (`config/DataSeeder`) instancia todo a través de los services y sus DTOs (nunca con
`new Categoria(...)`), así que también ejercita las validaciones de negocio. Con
`app.seed.log-resumen=true` (valor por defecto en `application.properties`) el arranque loguea un
resumen de lo cargado.

## Mapeo consigna → evidencia

| # | Punto de la consigna | Cómo se cumple |
|---|---|---|
| 1 | Dependencia de Swagger | `build.gradle` (`springdoc-openapi-starter-webmvc-ui:2.8.6`) + Swagger UI operativo en `/swagger-ui.html` |
| 2 | Capa Repository | `repository/` — 4 interfaces `@Repository extends JpaRepository` |
| 3 | Capa Service | `service/` — 4 `@Service` con `@Transactional`, lógica de negocio y transiciones de estado |
| 4 | Capa Controller + AdviceController | `controller/` — 4 `@RestController` + `exception/GlobalExceptionHandler` (`@RestControllerAdvice`) |
| 5a | 2 usuarios vía DTO/Postman | `POST /api/usuarios` × 2 (o `app.seed.enabled=true`) |
| 5b | 3 pedidos con ≥2 detalles | `POST /api/pedidos` × 3 (o seeder) |
| 5c | 3 categorías | `POST /api/categorias` × 3 (o seeder) |
| 5d | 10 productos | `POST /api/productos` × 10 (o seeder) |
| 6 | Actualizar 1 categoría | `PUT /api/categorias/{id}` (y `PATCH /api/categorias/{id}` para edición parcial) |
| 7 | Buscar usuario por id y mostrar por consola | `GET /api/usuarios/{id}` — `UsuarioService.buscarPorId` loguea por SLF4J id/nombre/apellido/mail/celular/rol/cantidadPedidos |
| 8 | Buscar usuario por mail y mostrar por consola | `GET /api/usuarios/buscar?mail=` — `UsuarioService.buscarPorMail` loguea de la misma forma |
| 9 | Swagger funcionando | `/swagger-ui.html` con los 4 tags (Categorías, Productos, Usuarios, Pedidos) y 30 operaciones documentadas |
| — | Métodos HTTP GET/POST/PUT/PATCH/DELETE | Ver tabla de contrato REST arriba — los 4 controllers cubren los 5 métodos |

## Convenciones del proyecto

- **Inyección por constructor únicamente** (campos `final`, sin `@Autowired`, sin field injection).
- **Baja lógica siempre**: `DELETE` marca `eliminado = true`; nunca borrado físico. Las queries
  activas usan `findByIdAndEliminadoFalse` / `findByEliminadoFalse`.
- **Relaciones bidireccionales**: el FK vive en el lado hijo (`@ManyToOne`), el padre usa
  `mappedBy`, y los helpers de entidad (`addDetallePedido`, `addPedido`, `addProducto`)
  sincronizan ambos lados.
- **DTOs como `record`**, agrupados por agregado en `dto/<agregado>/`. Los DTOs `*Edit`/`*Patch`
  no llevan `id` — en REST el id viaja por el path.
- **Mappers manuales** `@Component` (sin MapStruct/ModelMapper), sin dependencias de
  `repository/`.
- **Servicios como frontera transaccional**: lecturas `@Transactional(readOnly = true)`,
  escrituras `@Transactional`. Los controllers **nunca** ven entidades, solo DTOs.
- **Excepciones de dominio**: `RecursoNoEncontradoException` (404) y `ReglaNegocioException`
  (409), traducidas centralmente por `GlobalExceptionHandler`.

## Testing

Solo existe el smoke test generado (`Tp2SpringBootApplicationTests`, carga de contexto) — no hay
suite de tests por service/repository todavía.
