# Guia de instalacion y ejecucion

## Requisitos

- Docker Desktop.
- Docker Compose.
- Puertos libres: `8040`, `8091`, `8092`, `8093`, `8510`, `3310`.

## Levantar el proyecto

```bash
docker compose up -d --build
```

## Ver estado

```bash
docker compose ps
```

Todos los servicios principales deben quedar `Up`:

- `examen-ms-valencia-lib-libros`
- `examen-ms-valencia-lib-prestamos`
- `examen-ms-valencia-lib-reserva`
- `examen-api-gateway`
- `examen-mysql`
- `examen-consul`

## Probar endpoints

```bash
curl http://localhost:8040/api/libros
curl http://localhost:8040/api/prestamos
curl http://localhost:8040/api/reservas
```

## Apagar

```bash
docker compose down
```

## Reiniciar desde cero, eliminando datos

```bash
docker compose down -v
docker compose up -d --build
```
