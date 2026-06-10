package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import pe.edu.upeu.client.LibroClient;
import pe.edu.upeu.dto.LibroDTO;
import pe.edu.upeu.entity.HistorialReserva;
import pe.edu.upeu.entity.Reserva;
import pe.edu.upeu.errors.*;
import pe.edu.upeu.repository.ReservaRepository;
import pe.edu.upeu.services.ReservaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ReservaServiceImpl implements ReservaService {

    private static final Set<String> ESTADOS = Set.of("PENDIENTE", "CONFIRMADA", "CANCELADA", "EXPIRADA");

    @Inject
    ReservaRepository repository;

    @Inject
    @RestClient
    LibroClient libroClient;

    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "fallbackLibro")
    @Timeout(3000)
    public LibroDTO obtenerLibro(Long id) {
        return libroClient.buscarLibroPorId(id);
    }

    public LibroDTO fallbackLibro(Long id) {
        LibroDTO libro = new LibroDTO();
        libro.idLibro = id;
        libro.estado = "NO_DISPONIBLE";
        libro.stockDisponible = 0;
        return libro;
    }

    @Override
    @Transactional
    public Reserva create(Reserva reserva) {
        validarReserva(reserva);
        validarLibroDisponible(reserva.getIdLibro());

        if (reserva.getFechaReserva() == null) {
            reserva.setFechaReserva(LocalDate.now());
        }
        if (reserva.getEstado() == null || reserva.getEstado().isBlank()) {
            reserva.setEstado("PENDIENTE");
        }

        registrarCambio(reserva, null, reserva.getEstado(), "Reserva creada");
        repository.persist(reserva);
        return reserva;
    }

    @Override
    public List<Reserva> findAll() {
        return repository.listAll();
    }

    @Override
    public Reserva findById(Long id) {
        Reserva reserva = repository.findById(id);
        if (reserva == null) {
            throw new NotFoundException("Reserva no encontrada con id: " + id);
        }
        return reserva;
    }

    @Override
    @Transactional
    public Reserva update(Long id, Reserva reserva) {
        validarReserva(reserva);
        Reserva entity = findById(id);
        validarLibroDisponible(reserva.getIdLibro());

        entity.setNombreUsuario(reserva.getNombreUsuario());
        entity.setIdLibro(reserva.getIdLibro());
        entity.setFechaReserva(reserva.getFechaReserva());
        entity.setFechaExpiracion(reserva.getFechaExpiracion());

        if (reserva.getEstado() != null && !reserva.getEstado().isBlank() && !reserva.getEstado().equals(entity.getEstado())) {
            cambiarEstado(entity, reserva.getEstado(), "Reserva actualizada");
        }
        return entity;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Reserva reserva = findById(id);
        cambiarEstado(reserva, "CANCELADA", "Reserva eliminada logicamente");
    }

    @Override
    @Transactional
    public Reserva confirmar(Long id) {
        Reserva reserva = findById(id);
        validarLibroDisponible(reserva.getIdLibro());
        cambiarEstado(reserva, "CONFIRMADA", "Reserva confirmada");
        return reserva;
    }

    @Override
    @Transactional
    public Reserva cancelar(Long id) {
        Reserva reserva = findById(id);
        cambiarEstado(reserva, "CANCELADA", "Reserva cancelada");
        return reserva;
    }

    @Override
    @Transactional
    public Reserva expirar(Long id) {
        Reserva reserva = findById(id);
        cambiarEstado(reserva, "EXPIRADA", "Reserva expirada");
        return reserva;
    }

    @Override
    @Transactional
    public Reserva registrarHistorial(Long id, String estadoNuevo, String observacion) {
        Reserva reserva = findById(id);
        cambiarEstado(reserva, estadoNuevo, observacion);
        return reserva;
    }

    private void validarReserva(Reserva reserva) {
        if (reserva.getNombreUsuario() == null || reserva.getNombreUsuario().isBlank()) {
            throw new BadRequestException("El nombre del usuario es obligatorio");
        }
        if (reserva.getIdLibro() == null) {
            throw new BadRequestException("El libro es obligatorio");
        }
        if (reserva.getFechaExpiracion() == null) {
            throw new BadRequestException("La fecha de expiracion es obligatoria");
        }
        if (reserva.getEstado() != null && !reserva.getEstado().isBlank()) {
            validarEstado(reserva.getEstado());
        }
    }

    private void validarLibroDisponible(Long idLibro) {
        LibroDTO libro = obtenerLibro(idLibro);
        if (libro == null || !"DISPONIBLE".equals(libro.estado)) {
            throw new BadRequestException("El libro no existe o no esta disponible");
        }
        if (libro.stockDisponible == null || libro.stockDisponible <= 0) {
            throw new BadRequestException("El libro no tiene stock disponible");
        }
    }

    private void cambiarEstado(Reserva reserva, String estadoNuevo, String observacion) {
        validarEstado(estadoNuevo);
        String anterior = reserva.getEstado();
        reserva.setEstado(estadoNuevo);
        registrarCambio(reserva, anterior, estadoNuevo, observacion);
    }

    private void registrarCambio(Reserva reserva, String anterior, String nuevo, String observacion) {
        HistorialReserva historial = new HistorialReserva();
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(nuevo);
        historial.setObservacion(observacion);
        reserva.addHistorial(historial);
    }

    private void validarEstado(String estado) {
        if (!ESTADOS.contains(estado)) {
            throw new BadRequestException("Estado de reserva no valido: " + estado);
        }
    }
}
