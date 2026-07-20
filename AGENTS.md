# 3-Spring-API - Agents Context (AGENTS.md)

Este archivo proporciona contexto técnico e histórico del desarrollo del módulo `Customer` y `User` (Seguridad JWT) para agentes autónomos.

## 1. Tecnologías y Versiones
- **Java**: 25 (OpenJDK 25.0.2)
- **Spring Boot**: 4.1.0 (con Spring Security y JJWT 0.13.0)
- **Base de Datos**: H2 en memoria (`jdbc:h2:mem:testdb`)
- **Autenticación**: Stateless JWT alojado en Cookies HttpOnly.

## 2. Arquitectura de Seguridad y Roles
- **JwtAuthenticationFilter**: Intercepta cookies `jwt_token`, extrae y valida el JWT.
- **Roles**:
  - `ROLE_USER`: Solo lectura de clientes (`GET /api/customers/**`).
  - `ROLE_ADMIN`: Escritura/Gestión de clientes y administración de usuarios.
- **Seeder**: Carga inicial automática de `admin@example.com` (ADMIN) y `user@example.com` (USER).

## 3. Endpoints de la API
| Método | Endpoint | Entrada | Salida | Permisos / Observaciones |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | `LoginRequest` | `200` + Cookie | Público. Inicia sesión y genera token. |
| **POST** | `/api/auth/logout`| Ninguna | `204` | Público. Limpia la cookie HttpOnly. |
| **GET** | `/api/auth/me` | Ninguna | `200 OK` (UserDto)| Autenticado. Información de perfil. |
| **POST** | `/api/users` | `UserCreateDto`| `201` (UserDto)| Público. Auto-registro de usuarios. |
| **GET** | `/api/users` | Ninguna | `200` (List) | Solo `ADMIN`. Lista todos los usuarios. |
| **GET** | `/api/customers` | Ninguna | `200` (List) | `USER` y `ADMIN`. Lista clientes. |
| **POST** | `/api/customers` | `CustomerDto` | `201` (Customer)| Solo `ADMIN`. Registra cliente. |
| **PUT/DEL**| `/api/customers/{id}`| `CustomerDto` / id | `200` / `204` | Solo `ADMIN`. Modifica/Elimina cliente. |

## 4. Control de Excepciones y Auditoría
- **Auditoría**: JPA `@PrePersist` y `@PreUpdate` en entidades para control de auditoría en base de datos.
- **Excepciones en GlobalExceptionHandler**:
  - `CustomerNotFoundException` -> `404 Not Found`.
  - `EmailAlreadyExistsException` -> `400 Bad Request`.
  - `AuthenticationException` (credenciales inválidas) -> `401 Unauthorized`.
  - `MethodArgumentNotValidException` -> `400 Bad Request` (errores de validación de campos).

## 5. Archivos Principales
- Seguridad: [SecurityConfig.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/main/java/com/andres/course/agy/springboot/springapi/app/config/SecurityConfig.java) \| [JwtService.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/main/java/com/andres/course/agy/springboot/springapi/app/security/JwtService.java)
- Entidades: [Customer.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/main/java/com/andres/course/agy/springboot/springapi/app/models/Customer.java) \| [User.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/main/java/com/andres/course/agy/springboot/springapi/app/models/User.java)
- Controladores: [AuthController.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/main/java/com/andres/course/agy/springboot/springapi/app/controllers/AuthController.java) \| [UserController.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/main/java/com/andres/course/agy/springboot/springapi/app/controllers/UserController.java)
- Pruebas: [SecurityIntegrationTests.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/test/java/com/andres/course/agy/springboot/springapi/app/controllers/SecurityIntegrationTests.java)

## 6. Repositorio Remoto
- **GitHub Repository**: [curso-spring-agy-backend](https://github.com/andresguzf/curso-spring-agy-backend)

