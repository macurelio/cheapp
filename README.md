# cheapp


## Descripción

Este microservicio implementa la funcionalidad de identidad (usuarios, autenticación y asignación de roles) usando una arquitectura hexagonal (puertos y adaptadores). Está escrito en Java con Spring Boot y sigue principios de separación entre la lógica de negocio y las infraestructuras (persistencia, seguridad, publicación de eventos).


## Visión general de la arquitectura

- Dominio: modelos inmutables (User, Role, Permission) y eventos de dominio (DomainEvent, UserCreatedEvent, RoleAssignedEvent).
- Casos de uso (application / service): implementan la lógica de negocio y usan puertos (interfaces) para interactuar con infraestructuras.
- Puertos (out): interfaces que describen dependencias externas necesarias por la lógica de negocio (UserRepositoryPort, RoleRepositoryPort, PasswordEncoderPort, JwtProviderPort, EventPublisherPort).
- Adaptadores (out): implementaciones concretas de los puertos, por ejemplo for JPA (UserRepositoryAdapter, RoleRepositoryAdapter), para JWT (JjwtJwtProviderAdapter), para encriptación (BCryptPasswordEncoderAdapter) y para publicación de eventos (LoggingEventPublisherAdapter).
- Adaptadores (in / web): controladores REST que exponen los casos de uso (AuthController, UserController) y definen DTOs para request/response.
- Config: las beans que conectan puertos con adaptadores y crean los casos de uso están en `IdentityBeansConfig`.


## Flujo de datos y lógica de negocio (por caso de uso)

1) Crear usuario (endpoint: POST /users)
   - Request: `CreateUserRequest { email, password }`.
   - Flujo:
     - `UserController.create` recibe el request y delega a `CreateUserUseCase` (implementado por `CreateUserService`).
     - `CreateUserService` valida que no exista un usuario con el mismo email usando `UserRepositoryPort.existsByEmail`.
     - Si existe, lanza `ConflictException`.
     - Si no existe, codifica la contraseña usando `PasswordEncoderPort.encode` (BCrypt) y crea un `User` del dominio.
     - Persiste el usuario con `UserRepositoryPort.save`.
     - Publica un evento `UserCreatedEvent` usando `EventPublisherPort.publish`.
     - Responde con `CreateUserResponse { id, email }` y HTTP 201.

2) Obtener usuario (endpoint: GET /users/{id})
   - Flujo:
     - `UserController.getById` invoca `GetUserUseCase.getById`.
     - `GetUserService` solicita el usuario a `UserRepositoryPort.findById`.
     - Si no existe, lanza `NotFoundException`.
     - Convierte las roles a un Set<String> con los nombres y devuelve `UserResponse { id, email, enabled, roles }`.

3) Actualizar usuario (endpoint: PUT /users/{id})
   - Request: `UpdateUserRequest { email?, enabled? }`.
   - Flujo:
     - `UserController.update` llama a `UpdateUserUseCase.update`.
     - `UpdateUserService` recupera el usuario con `findById` (si no existe -> `NotFoundException`).
     - Si se cambia email, valida que el nuevo email no exista (`existsByEmail`) o lanza `ConflictException`.
     - Crea una nueva instancia `User` con los cambios (inmutabilidad) y guarda con `save`.
     - Responde con HTTP 204 No Content.

4) Asignar rol (endpoint: POST /users/{id}/roles)
   - Request: `AssignRoleRequest { roleName }`.
   - Flujo:
     - `UserController.assignRole` llama a `AssignRoleUseCase.assign`.
     - `AssignRoleService` obtiene el usuario y el rol mediante `UserRepositoryPort.findById` y `RoleRepositoryPort.findByName` (si no existen -> `NotFoundException`).
     - Añade el rol al conjunto de roles (LinkedHashSet para mantener orden) y persiste el usuario actualizado.
     - Publica `RoleAssignedEvent` usando `EventPublisherPort`.
     - Responde HTTP 204 No Content.

5) Desasignar rol (endpoint: DELETE /users/{id}/roles/{roleName})
   - Flujo:
     - `UserController.unassignRole` llama a `UnassignRoleUseCase.unassign`.
     - `UnassignRoleService` obtiene el usuario y el rol; intenta removerlo del conjunto de roles.
     - Si no estaba presente, no hace nada; si se removió, guarda el usuario actualizado.
     - Responde HTTP 204 No Content.

6) Autenticación (endpoint: POST /auth/login)
   - Request: `LoginRequest { email, password }`.
   - Flujo:
     - `AuthController.login` delega a `AuthenticateUserUseCase.authenticate`.
     - `AuthenticateUserService` busca el usuario por email (`findByEmail`). Si no existe o credenciales no coinciden, lanza `UnauthorizedException`.
     - Verifica `enabled`.
     - Verifica la contraseña con `PasswordEncoderPort.matches`.
     - Construye un token JWT usando `JwtProviderPort.createToken(userId, email, roles)`.
     - Devuelve `LoginResponse { accessToken, tokenType, expiresInSeconds, userId, email, roles }`.


## Modelos de dominio principales

- User
  - Campos: id, email, passwordHash, enabled, roles (Set<Role>), createdAt
  - Inmutable; métodos `withId`, `withRoles` para crear nuevas instancias con cambios.

- Role
  - Representa un rol con nombre y conjunto de permisos (ver `Role` y `Permission` en `domain.model`).

- Events
  - `DomainEvent` (interfaz) con `occurredAt()`.
  - `UserCreatedEvent(userId, email, occurredAt)` y `RoleAssignedEvent(userId, roleName, occurredAt)`.
  - Actualmente se publican a logs mediante `LoggingEventPublisherAdapter`.


## Puertos (interfaces) importantes

- UserRepositoryPort
  - save(User), findById(Long), findByEmail(String), existsByEmail(String)

- RoleRepositoryPort
  - findByName(String)

- PasswordEncoderPort
  - encode(raw), matches(raw, encoded)

- JwtProviderPort
  - createToken(userId, email, roles) -> Token(value, expiresInSeconds)
  - decodeAndValidate(token) -> DecodedToken(userId, email, roles)

- EventPublisherPort
  - publish(DomainEvent)


## Adaptadores (implementaciones)

- Persistencia JPA: `UserRepositoryAdapter`, `RoleRepositoryAdapter` (con `UserSpringDataRepository` / `RoleSpringDataRepository`).
- Seguridad / bcrypt: `BCryptPasswordEncoderAdapter` (usa Spring Security `BCryptPasswordEncoder`).
- JWT: `JjwtJwtProviderAdapter` (configurado por `JwtProperties`).
- Event publishing: `LoggingEventPublisherAdapter` (actualmente solo escribe en logs; puede reemplazarse por outbox/Kafka más adelante).

La conexión entre puertos y adaptadores y la creación de casos de uso se hace en `IdentityBeansConfig`.


## Excepciones y manejo de errores

- `ConflictException` -> retornos de error cuando hay conflicto (por ejemplo email ya registrado).
- `NotFoundException` -> cuando recursos (usuario/rol) no existen.
- `UnauthorizedException` -> credenciales inválidas o usuario deshabilitado.

Spring Boot mapea estas excepciones a respuestas HTTP apropiadas (ver manejadores globales si existen en el código de la aplicación).


## Cómo ejecutar y probar localmente

Prerequisitos:
- JDK 17+ (o la versión configurada en `pom.xml`).
- Maven

Ejecutar la aplicación:

```bash
./mvnw spring-boot:run
```

Endpoints principales (JSON):
- POST /users
  - Body: { "email": "user@example.com", "password": "secret123" }
  - Respuesta: 201 { "id": 1, "email": "user@example.com" }

- GET /users/{id}
  - Respuesta: 200 { "id": 1, "email": "user@example.com", "enabled": true, "roles": ["ADMIN"] }

- PUT /users/{id}
  - Body: { "email": "new@example.com", "enabled": true }
  - Respuesta: 204 No Content

- POST /users/{id}/roles
  - Body: { "roleName": "ADMIN" }
  - Respuesta: 204 No Content

- DELETE /users/{id}/roles/{roleName}
  - Respuesta: 204 No Content

- POST /auth/login
  - Body: { "email": "user@example.com", "password": "secret123" }
  - Respuesta: 200 { "accessToken": "...", "tokenType": "Bearer", "expiresInSeconds": 3600, "userId": 1, "email": "user@example.com", "roles": ["ADMIN"] }


## Notas de implementación y próximos pasos recomendados

- Event publishing: actualmente solo escribe a logs; considerar Outbox pattern o publicador a Kafka/RabbitMQ para integraciones.
- Manejo de contraseñas: BCrypt con parámetros por defecto; considerar ajustar fuerza o usar un servicio de gestión de secretos.
- Tests: ya existen tests en `src/test` para servicios clave; añadir pruebas de integración que usen una base de datos en memoria y validen los endpoints.
- Seguridad: los endpoints de administración (asignación de roles) deberían protegerse (JWT + roles) a nivel del controller/seguridad de Spring.


## Mapeo rápido de archivos relevantes

- src/main/java/com/cheapp/cheapp/identity/adapters/in/web/* : `UserController`, `AuthController`, DTOs.
- src/main/java/com/cheapp/cheapp/identity/application/service/* : implementaciones de casos de uso.
- src/main/java/com/cheapp/cheapp/identity/application/port/in/* and /out/* : interfaces de use cases y puertos.
- src/main/java/com/cheapp/cheapp/identity/adapters/out/* : adaptadores (persistence, security, jwt, event).
- src/main/java/com/cheapp/cheapp/identity/domain/* : modelos y eventos de dominio.
- src/main/java/com/cheapp/cheapp/identity/config/IdentityBeansConfig.java : wiring de beans.


---

Si quieres, puedo:
- Añadir ejemplos de requests con curl o httpie.
- Generar un Postman collection / OpenAPI más detallado.
- Añadir un apartado de pruebas de integración o ajustar README para despliegue.

Fin de la actualización del README.
