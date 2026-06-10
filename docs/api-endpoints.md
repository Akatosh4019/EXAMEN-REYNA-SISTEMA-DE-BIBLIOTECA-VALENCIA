# Documentacion de API

## Swagger / OpenAPI

Con Docker levantado:

- Libros Swagger UI: `http://localhost:8092/q/swagger-ui`
- Libros OpenAPI JSON/YAML: `http://localhost:8092/q/openapi`
- Prestamos Swagger UI: `http://localhost:8091/q/swagger-ui`
- Prestamos OpenAPI JSON/YAML: `http://localhost:8091/q/openapi`
- Reserva Swagger UI: `http://localhost:8093/q/swagger-ui`
- Reserva OpenAPI JSON/YAML: `http://localhost:8093/q/openapi`

## Gateway

- `GET http://localhost:8040/api/libros`
- `GET http://localhost:8040/api/categorias`
- `GET http://localhost:8040/api/autores`
- `GET http://localhost:8040/api/prestamos`
- `GET http://localhost:8040/api/reservas`

## Flujo de prueba recomendado

1. Listar categorias.
2. Listar autores.
3. Registrar libro.
4. Consultar stock.
5. Registrar prestamo.
6. Consultar stock otra vez.
7. Registrar devolucion.
8. Crear reserva.
9. Confirmar reserva.
10. Listar reservas para ver historial.
