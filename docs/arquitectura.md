# Arquitectura y despliegue

## Microservicios

```mermaid
flowchart TB
    gateway["API Gateway :8040"]
    consul["Consul :8510"]
    mysql["MySQL Docker :3310"]

    libros["ms-valencia-lib-libros :8092"]
    prestamos["ms-valencia-lib-prestamos :8091"]
    reserva["ms-valencia-lib-reserva :8093"]

    dbLibros[("libros_db")]
    dbPrestamos[("prestamos_db")]
    dbReserva[("reserva_db")]

    gateway --> libros
    gateway --> prestamos
    gateway --> reserva

    prestamos -- REST --> libros
    reserva -- REST --> libros

    libros --> dbLibros
    prestamos --> dbPrestamos
    reserva --> dbReserva

    mysql --> dbLibros
    mysql --> dbPrestamos
    mysql --> dbReserva

    consul -. registra/descubre .- gateway
    consul -. registra/descubre .- libros
    consul -. registra/descubre .- prestamos
    consul -. registra/descubre .- reserva
```

## Comunicaciones REST

- `ms-valencia-lib-prestamos -> ms-valencia-lib-libros`
  - Verifica libro.
  - Valida stock.
  - Disminuye stock al prestar.
  - Aumenta stock al devolver.

- `ms-valencia-lib-reserva -> ms-valencia-lib-libros`
  - Verifica libro.
  - Valida disponibilidad.

## Bases de datos

- `libros_db`: `categoria`, `autor`, `libro`.
- `prestamos_db`: `prestamo`, `detalle_prestamo`, `devolucion`.
- `reserva_db`: `reserva`, `historial_reserva`.
