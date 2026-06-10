package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import pe.edu.upeu.client.LibroClient;
import pe.edu.upeu.dto.LibroDTO;
import pe.edu.upeu.entity.DetallePrestamo;
import pe.edu.upeu.entity.Devolucion;
import pe.edu.upeu.entity.Prestamo;
import pe.edu.upeu.errors.*;
import pe.edu.upeu.repository.PrestamoRepository;
import pe.edu.upeu.services.PrestamoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class PrestamoServiceImpl implements PrestamoService {

    private static final Set<String> ESTADOS = Set.of("ACTIVO", "DEVUELTO", "VENCIDO", "CANCELADO");

    @Inject
    PrestamoRepository repository;

    @Inject
    @RestClient
    LibroClient libroClient;

    @Override
    @Transactional
    public Prestamo create(Prestamo prestamo) {
        validarPrestamo(prestamo);
        prestamo.getDetalles().forEach(this::validarLibroConStock);
        prestamo.getDetalles().forEach(detalle -> libroClient.disminuirStock(detalle.getIdLibro(), detalle.getCantidad()));

        if (prestamo.getEstado() == null || prestamo.getEstado().isBlank()) {
            prestamo.setEstado("ACTIVO");
        }
        if (prestamo.getFechaPrestamo() == null) {
            prestamo.setFechaPrestamo(LocalDate.now());
        }

        repository.persist(prestamo);
        return prestamo;
    }

    @Override
    public List<Prestamo> findAll() {
        return repository.listAll();
    }

    @Override
    public Prestamo findById(Long id) {
        Prestamo prestamo = repository.findById(id);
        if (prestamo == null) {
            throw new NotFoundException("Prestamo no encontrado con id: " + id);
        }
        return prestamo;
    }

    @Override
    @Transactional
    public Prestamo update(Long id, Prestamo prestamo) {
        validarPrestamo(prestamo);
        Prestamo entity = findById(id);

        if ("DEVUELTO".equals(entity.getEstado())) {
            throw new BadRequestException("No se puede editar un prestamo devuelto");
        }

        entity.setNombreUsuario(prestamo.getNombreUsuario());
        entity.setFechaPrestamo(prestamo.getFechaPrestamo());
        entity.setFechaVencimiento(prestamo.getFechaVencimiento());
        entity.setFechaDevolucion(prestamo.getFechaDevolucion());
        if (prestamo.getEstado() != null && !prestamo.getEstado().isBlank()) {
            validarEstadoPrestamo(prestamo.getEstado());
            entity.setEstado(prestamo.getEstado());
        }
        return entity;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Prestamo prestamo = findById(id);
        prestamo.setEstado("CANCELADO");
    }

    @Override
    @Transactional
    public Devolucion registrarDevolucion(Long id, Devolucion devolucion) {
        Prestamo prestamo = findById(id);
        if ("DEVUELTO".equals(prestamo.getEstado())) {
            throw new BadRequestException("El prestamo ya fue devuelto");
        }
        if ("CANCELADO".equals(prestamo.getEstado())) {
            throw new BadRequestException("No se puede devolver un prestamo cancelado");
        }

        LocalDate fecha = devolucion.getFechaDevolucion() == null ? LocalDate.now() : devolucion.getFechaDevolucion();
        devolucion.setFechaDevolucion(fecha);
        devolucion.setEstado(fecha.isAfter(prestamo.getFechaVencimiento()) ? "ATRASADO" : "A_TIEMPO");

        prestamo.getDetalles().forEach(detalle -> libroClient.aumentarStock(detalle.getIdLibro(), detalle.getCantidad()));
        prestamo.setFechaDevolucion(fecha);
        prestamo.setEstado("DEVUELTO");
        prestamo.setDevolucion(devolucion);
        return devolucion;
    }

    @Override
    @Transactional
    public Prestamo cambiarEstado(Long id, String estado) {
        validarEstadoPrestamo(estado);
        Prestamo prestamo = findById(id);
        prestamo.setEstado(estado);
        return prestamo;
    }

    private void validarPrestamo(Prestamo prestamo) {
        if (prestamo.getNombreUsuario() == null || prestamo.getNombreUsuario().isBlank()) {
            throw new BadRequestException("El nombre del usuario es obligatorio");
        }
        if (prestamo.getFechaVencimiento() == null) {
            throw new BadRequestException("La fecha de vencimiento es obligatoria");
        }
        if (prestamo.getDetalles() == null || prestamo.getDetalles().isEmpty()) {
            throw new BadRequestException("El prestamo debe tener al menos un detalle");
        }
        if (prestamo.getEstado() != null && !prestamo.getEstado().isBlank()) {
            validarEstadoPrestamo(prestamo.getEstado());
        }
        prestamo.getDetalles().forEach(detalle -> {
            if (detalle.getIdLibro() == null) {
                throw new BadRequestException("El libro es obligatorio en el detalle");
            }
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new BadRequestException("La cantidad del detalle debe ser mayor a 0");
            }
        });
    }

    private void validarLibroConStock(DetallePrestamo detalle) {
        LibroDTO libro = libroClient.buscarLibroPorId(detalle.getIdLibro());
        if (libro == null || !"DISPONIBLE".equals(libro.estado)) {
            throw new BadRequestException("El libro no existe o no esta disponible: " + detalle.getIdLibro());
        }
        if (libro.stockDisponible == null || libro.stockDisponible < detalle.getCantidad()) {
            throw new BadRequestException("No hay stock suficiente para el libro: " + detalle.getIdLibro());
        }
    }

    private void validarEstadoPrestamo(String estado) {
        if (!ESTADOS.contains(estado)) {
            throw new BadRequestException("Estado de prestamo no valido: " + estado);
        }
    }
}
