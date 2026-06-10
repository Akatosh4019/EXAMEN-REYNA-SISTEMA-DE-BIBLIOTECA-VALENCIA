# Pruebas

## Pruebas con Postman

La coleccion esta en:

`postman/ms-valencia-lib.postman_collection.json`

Importar en Postman y ejecutar en este orden:

1. Listar categorias.
2. Listar autores.
3. Registrar libro.
4. Listar libros.
5. Consultar stock.
6. Registrar prestamo.
7. Consultar stock para comprobar descuento.
8. Registrar devolucion.
9. Crear reserva.
10. Confirmar reserva.
11. Listar reservas para revisar historial.

## Pruebas de integracion manual

Las pruebas de integracion principales son:

- Prestamos llama por REST a Libros para disminuir stock.
- Prestamos llama por REST a Libros para aumentar stock en devolucion.
- Reserva llama por REST a Libros para validar existencia y disponibilidad.
- API Gateway enruta a los tres microservicios.

## Nota sobre pruebas unitarias

El proyecto conserva estructura de pruebas Maven/Quarkus/Spring. Para una entrega mas formal, se pueden agregar pruebas unitarias por servicio sobre las reglas de negocio principales.
