# LAFORSE-HG – Sistema de gestión de solicitudes y casos periciales

Proyecto del curso Marcos de Desarrollo Web – **Avance 3: integración Frontend + Backend + Base de datos**.
Spring Boot 3.3 · Thymeleaf · Bootstrap 5 · Spring Data JPA (Hibernate) · MySQL 8 · Bean Validation.

## 1. Preparar MySQL
1. Abrir MySQL Workbench y ejecutar `database/laforse_db.sql` (crea `laforse_db`, las 4 tablas y los datos iniciales).
2. Revisar usuario y clave en `src/main/resources/application.properties`:

       spring.datasource.username=root
       spring.datasource.password=root

> Si no se ejecuta el script, la aplicación crea la BD (`createDatabaseIfNotExist=true`), Hibernate crea las tablas
> (`ddl-auto=update`) y `DataSeeder` carga los mismos datos de ejemplo.

## 2. Ejecutar
- NetBeans: abrir la carpeta del proyecto → **Run** (usa `LaforseApplication`).
- Consola: `mvn spring-boot:run`

Abrir http://localhost:8080

## 3. Pruebas automáticas
    mvn test

Las pruebas usan H2 en memoria en modo MySQL (`src/test/resources/application.properties`), por lo que no tocan la BD real.
Incluyen: páginas, formulario de contacto, CRUD de casos y peritos, reglas de negocio y API REST.

## Rutas
| Método y ruta | Descripción |
|---|---|
| GET / , /nosotros, /servicios, /servicios/{id}, /herramientas | Páginas informativas (servicios leídos desde MySQL) |
| GET/POST /contacto | Formulario público: guarda la solicitud en la tabla `solicitud` |
| GET /casos?estado=&q= | Listado de casos con filtro por estado y búsqueda |
| GET /casos/nuevo[?solicitud=id] · POST /casos | Registrar caso (desde cero o a partir de una solicitud) |
| GET /casos/{id}/editar · POST /casos/{id} | Editar caso |
| POST /casos/{id}/eliminar | Eliminar caso (con modal de confirmación) |
| GET /peritos · /peritos/nuevo · /peritos/{id}/editar · POST /peritos · POST /peritos/{id}/eliminar | CRUD de peritos |
| GET /solicitudes · POST /solicitudes/{id}/estado · POST /solicitudes/{id}/eliminar | Bandeja de solicitudes |
| GET/POST /api/casos · GET/PUT/DELETE /api/casos/{id} | API REST en JSON (ver `docs/LAFORSE-API.postman_collection.json`) |

## Reglas de negocio
- El número de expediente (`PJ-AAAA-NNNN`) y el correo del perito son únicos.
- La fecha de inicio de un caso no puede ser futura.
- Solo se asignan casos nuevos a peritos activos.
- Un caso con **Dictamen entregado** queda cerrado: no se edita ni se elimina.
- No se elimina un perito con casos asignados (se desactiva).
- Una solicitud que generó un caso no se elimina; al abrir el caso pasa a **Atendida**.
- Una solicitud con urgencia **Crítica** abre el caso con estado **Urgente**.

## Estructura
- `model/` – entidades JPA `Servicio`, `Perito`, `Solicitud`, `Caso` + enums y `ContactoForm`
- `repository/` – interfaces `JpaRepository`
- `service/` – lógica y reglas de negocio (`@Transactional`)
- `controller/` – vistas Thymeleaf; `controller/api/` – `CasoRestController`
- `dto/` – `CasoForm` (formulario/JSON) y `CasoResponse`
- `exception/` – excepciones y manejadores (`WebExceptionHandler`, `ApiExceptionHandler`)
- `config/DataSeeder` – datos iniciales

## Entregables del Avance 3
| Archivo | Contenido |
|---|---|
| `docs/entregables/Avance03_ProyectoFinal_LAFORSE_Jens.pdf` (y `.docx`) | Informe: diseño de BD, servicios, integración, interfaces, pruebas, UML y anexos |
| `docs/entregables/Avance03_Exposicion_LAFORSE.pptx` | Presentación de 9 diapositivas (guion en las notas del orador) |
| `docs/entregables/Guion_Exposicion_Avance03_LAFORSE.docx` | Guion de exposición de 7 minutos y preguntas probables |

**Autor:** Jens Fabrizio Colchado Chavez · Universidad Tecnológica del Perú · Marcos de Desarrollo Web (100000S52T)
