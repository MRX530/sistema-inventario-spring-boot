# Sistema de Gestión de Inventario

Proyecto de portafolio: sistema web genérico de inventario con múltiples usuarios,
control de stock y reportes de movimientos.

## Arquitectura

El proyecto sigue una **arquitectura en capas**, el estándar para aplicaciones
empresariales pequeñas/medianas:

```
Frontend (HTML/JS)
      ↓ peticiones HTTP (fetch)
Controller (REST API)
      ↓
Service (lógica de negocio)
      ↓
Repository (acceso a datos, Spring Data JPA)
      ↓
Base de datos (MySQL)
```

**Por qué esta separación importa:** cada capa solo conoce a la de al lado. Si mañana
cambias el frontend por React, o la base de datos por PostgreSQL, el resto del sistema
no se entera. Esto es justo lo que un cliente freelance espera ver en un proyecto serio.

## Modelo de datos

- **Usuario** — pertenece a un **Rol** (ADMIN o EMPLEADO)
- **Categoría** — agrupa **Productos**
- **Producto** — tiene stock actual y stock mínimo
- **Movimiento** — registra cada entrada/salida de stock, ligado a un Producto y a un Usuario

La tabla `Movimiento` es la pieza clave: sin ella solo sabrías el stock *actual*,
pero no podrías generar reportes históricos.

## Estructura de carpetas

```
inventario-app/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/inventario/
│       │   ├── model/         → las 5 entidades + enum TipoMovimiento
│       │   ├── repository/    → interfaces JpaRepository (acceso a datos)
│       │   ├── service/       → lógica de negocio y validaciones
│       │   ├── controller/    → endpoints REST
│       │   ├── config/        → manejo global de errores
│       │   └── Application.java
│       └── resources/
│           └── application.properties
├── frontend/
│   ├── index.html      → login
│   ├── productos.html  → CRUD de productos + registrar movimientos
│   └── js/api.js        → funciones para hablar con la API
└── database/
    └── schema.sql       → datos de ejemplo
```

## Lógica de negocio clave (qué hace especial a este proyecto)

En `MovimientoService.registrarMovimiento()`:

1. Si el movimiento es **SALIDA**, valida que haya stock suficiente antes de restar.
   Si no hay, lanza `StockInsuficienteException` — que el `GlobalExceptionHandler`
   convierte en una respuesta HTTP clara (400 con mensaje) en vez de un error genérico.
2. Si es **ENTRADA**, suma al stock.
3. La operación está marcada `@Transactional`: actualiza el producto Y crea el
   movimiento como una sola unidad — si algo falla a mitad de camino, todo se revierte
   (no queda el stock actualizado sin el registro del movimiento, o viceversa).

Este tipo de regla — "no dejar que el sistema quede en un estado inconsistente" —
es exactamente lo que un cliente evalúa cuando revisa tu código.

## Cómo correrlo

### Requisitos
- Java 17+
- Maven
- MySQL corriendo localmente

### Pasos

1. Crea la base de datos (o deja que Hibernate la genere sola):
   ```sql
   CREATE DATABASE inventario_db;
   ```

2. Edita `backend/src/main/resources/application.properties` con tu usuario y
   contraseña de MySQL.

3. Desde la carpeta `backend/`, ejecuta:
   ```bash
   mvn spring-boot:run
   ```
   Esto levanta el servidor en `http://localhost:8080`. Hibernate crea las tablas
   automáticamente la primera vez (por `ddl-auto=update`).

4. (Opcional) Carga datos de ejemplo con `database/schema.sql`.

5. Abre `frontend/index.html` directamente en el navegador (o sírvelo con
   `python -m http.server` desde la carpeta `frontend/`).

6. Inicia sesión con el usuario de ejemplo: `admin@inventario.com` / `1234`.

## Endpoints principales

| Método | Ruta                          | Qué hace                              |
|--------|-------------------------------|----------------------------------------|
| GET    | /api/productos                 | Lista todos los productos              |
| GET    | /api/productos/stock-bajo     | Productos por debajo del mínimo        |
| POST   | /api/productos                 | Crea un producto                       |
| POST   | /api/movimientos               | Registra entrada/salida (actualiza stock) |
| GET    | /api/movimientos/reporte      | Movimientos entre dos fechas           |
| POST   | /api/usuarios/login             | Login de usuario                       |

## Qué mostrar de este proyecto en tu portafolio/perfil freelance

- Arquitectura en capas bien separada (no todo en un solo archivo)
- Regla de negocio real con validación (stock insuficiente)
- Manejo centralizado de errores (`@RestControllerAdvice`)
- Relaciones entre entidades con JPA (`@ManyToOne`)
- API REST consumida por un frontend real

## Mejoras aplicadas

**Contraseñas encriptadas (BCrypt)**
`UsuarioService` usa `PasswordEncoder` (configurado en `SecurityConfig`) para
encriptar el password al guardar (`encode()`) y compararlo al hacer login
(`matches()`). Nunca se guarda ni se compara texto plano.

**Autenticación con JWT**
- `POST /api/usuarios/login` ahora devuelve, además del usuario, un `token`.
- El frontend (`js/api.js`) lo guarda en `sessionStorage` y lo manda en el
  header `Authorization: Bearer <token>` en cada petición.
- `JwtAuthFilter` intercepta cada request y valida el token antes de dejarlo pasar.
- `SecurityConfig` deja públicas solo `/api/usuarios/login` y `/api/usuarios/registro`;
  todo lo demás requiere token válido.
- **Nota:** el registro de usuario se movió de `POST /api/usuarios` a
  `POST /api/usuarios/registro`.

**Paginación**
- Nuevo endpoint: `GET /api/productos/pagina?page=0&size=10&sort=nombre`
- Internamente usa `Pageable`, que Spring traduce a `LIMIT`/`OFFSET` en SQL —
  no trae todos los productos a memoria para cortar la lista después.
- El endpoint original `GET /api/productos` se mantiene sin cambios.

**Tests unitarios**
- `backend/src/test/java/com/inventario/service/MovimientoServiceTest.java`
- Usa Mockito para simular los repositorios (no se conecta a MySQL real).
- Cubre: salida con stock suficiente, salida con stock insuficiente (el caso
  que lanza `StockInsuficienteException`), entrada de stock, y producto inexistente.
- Para correrlos: `mvn test` desde la carpeta `backend/`.

## Próximos pasos sugeridos (para seguir robusteciendo)

- Mover la clave secreta de JWT (`JwtUtil`) a `application.properties` o una
  variable de entorno, en vez de tenerla fija en el código
- Agregar roles reales a la autorización (que solo ADMIN pueda borrar productos)
- Agregar tests de integración con una base de datos en memoria (H2)
- Refresh tokens, para no forzar re-login cada 8 horas
