# 3-Spring-API - Agents Context (AGENTS.md)

Este archivo proporciona contexto técnico e histórico del desarrollo del módulo `Customer` para agentes autónomos.

## 1. Tecnologías y Versiones
- **Java**: 25 (OpenJDK 25.0.2)
- **Spring Boot**: 4.1.0
- **Base de Datos**: H2 en memoria (`jdbc:h2:mem:testdb`)
- **Herramienta de Construcción**: Maven (Wrapper `./mvnw`)
- **Framework de Pruebas**: JUnit 5, Mockito

## 2. Arquitectura de Capas
El módulo sigue un patrón de arquitectura limpia en capas bajo la ruta base:
`com.andres.course.agy.springboot.springapi.app`

- **models**: Entidades de JPA con validación de Jakarta y hooks de auditoría (`createdAt`/`updatedAt`).
- **repositories**: Interfaces que extienden de `JpaRepository`.
- **dto**: Objetos de transferencia representados como Java `records` (excluyen fechas de auditoría).
- **mappers**: Componentes de conversión explícita entre entidades y DTOs.
- **services**: Lógica de negocio transaccional que procesa e interactúa únicamente con DTOs.
- **controllers**: Controladores REST que exponen endpoints HTTP usando `ResponseEntity`.
- **exceptions**: Excepciones de negocio personalizadas y controlador global de excepciones.

## 3. Endpoints de la API
Ruta base de recursos: `/api/customers`

| Método | Endpoint | Entrada | Salida (Éxito) | Errores Posibles |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/customers` | Ninguna | `200 OK` (Lista de DTOs) | `500` |
| **GET** | `/api/customers/{id}` | Parámetro de ruta `id` | `200 OK` (DTO) | `404` (Cliente no existe) |
| **POST** | `/api/customers` | JSON de `CustomerDto` | `201 Created` (DTO) | `400` (Validación o Email duplicado) |
| **PUT** | `/api/customers/{id}` | `id` + JSON de `CustomerDto` | `200 OK` (DTO) | `400` (Email duplicado) / `404` |
| **DELETE**| `/api/customers/{id}` | Parámetro de ruta `id` | `204 No Content` | `404` (Cliente no existe) |

## 4. Auditoría y Control de Excepciones
- **Auditoría**: Se emplean los métodos `@PrePersist` y `@PreUpdate` en la entidad `Customer` para sincronizar automáticamente los campos `createdAt` y `updatedAt`.
- **Excepciones**:
  - `CustomerNotFoundException` maps to `404 Not Found`.
  - `EmailAlreadyExistsException` maps to `400 Bad Request`.
  - `MethodArgumentNotValidException` maps to `400 Bad Request` con desglose de campos inválidos.

## 5. Archivos Principales
- Entidad: [Customer.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/main/java/com/andres/course/agy/springboot/springapi/app/models/Customer.java)
- Repositorio: [CustomerRepository.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/main/java/com/andres/course/agy/springboot/springapi/app/repositories/CustomerRepository.java)
- Excepciones: [GlobalExceptionHandler.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/main/java/com/andres/course/agy/springboot/springapi/app/exceptions/GlobalExceptionHandler.java)
- Pruebas unitarias: [CustomerServiceImplTests.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/test/java/com/andres/course/agy/springboot/springapi/app/services/CustomerServiceImplTests.java)
- Pruebas de integración: [CustomerRepositoryTests.java](file:///Users/andres/Desktop/SpringAntigravityAI/3-spring-api/src/test/java/com/andres/course/agy/springboot/springapi/app/repositories/CustomerRepositoryTests.java)
