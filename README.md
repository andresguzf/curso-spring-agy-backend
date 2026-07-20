# Curso Spring & Antigravity AI - Backend

Este proyecto es el backend para la aplicación de **Gestión de Clientes y Administración de Usuarios**, desarrollado en Java 25 utilizando **Spring Boot**.

## Características Principales

- **Arquitectura Limpia**: Estructurado en capas (controllers, services, repositories y models).
- **Seguridad Robusta**: Autenticación stateless basada en JWT (JSON Web Tokens) transmitidos de forma segura mediante Cookies HttpOnly.
- **Control de Acceso basado en Roles**:
  - `ROLE_USER`: Acceso de solo lectura a clientes y facturas (`GET /api/customers/**`, `GET /api/invoices/**`).
  - `ROLE_ADMIN`: Acceso total para la gestión de clientes, facturas y administración de usuarios.
- **Base de Datos**: H2 en memoria (`jdbc:h2:mem:testdb`) ideal para desarrollo y pruebas rápidas.
- **Auditoría Automática**: Implementación de JPA `@PrePersist` y `@PreUpdate` en las entidades de base de datos.
- **Manejo Global de Excepciones**: Respuestas uniformes y códigos de estado HTTP semánticos (400, 401, 404, etc.) para errores de negocio y validación.

## Requisitos de Ejecución

- **Java**: JDK 25.
- **Maven**: Incluido mediante el wrapper (`./mvnw`).

## Endpoints Principales de la API

| Método | Endpoint | Entrada | Salida | Permisos / Observaciones |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | `LoginRequest` | `200` + Cookie | Público. Inicia sesión y genera token. |
| **POST** | `/api/auth/logout`| Ninguna | `204` | Público. Limpia la cookie HttpOnly. |
| **GET** | `/api/auth/me` | Ninguna | `200 OK` (UserDto)| Autenticado. Información de perfil. |
| **POST** | `/api/users` | `UserCreateDto`| `201` (UserDto)| Público. Auto-registro de usuarios. |
| **GET** | `/api/users` | Ninguna | `200` (List) | Solo `ADMIN`. Lista todos los usuarios. |
| **GET** | `/api/customers` | Ninguna | `200` (List) | `USER` y `ADMIN`. Lista clientes. |
| **POST** | `/api/customers` | `CustomerDto` | `201` (Customer)| Solo `ADMIN`. Registra cliente. |
| **PUT/DEL**| `/api/customers/{id}`| `CustomerDto` / id | `200` / `204` | Solo `ADMIN`. Modifica o elimina cliente. |
| **GET** | `/api/invoices` | Ninguna | `200` (List) | `USER` y `ADMIN`. Lista todas las facturas. |
| **GET** | `/api/invoices/{id}`| Ninguna | `200` (InvoiceDto) | `USER` y `ADMIN`. Obtiene factura por ID. |
| **GET** | `/api/invoices/customer/{customerId}`| Ninguna | `200` (List)| `USER` y `ADMIN`. Obtiene facturas de un cliente. |
| **POST** | `/api/invoices` | `InvoiceDto` | `201` (InvoiceDto) | Solo `ADMIN`. Registra factura. |
| **PUT** | `/api/invoices/{id}`| `InvoiceDto` | `200` (InvoiceDto) | Solo `ADMIN`. Modifica factura. |
| **DELETE**| `/api/invoices/{id}`| Ninguna | `204` | Solo `ADMIN`. Elimina factura. |

## Desarrollo y Ejecución

Para levantar el servidor de desarrollo, ejecuta en la raíz del proyecto:
```bash
./mvnw spring-boot:run
```
El servidor arrancará por defecto en `http://localhost:8080`.
