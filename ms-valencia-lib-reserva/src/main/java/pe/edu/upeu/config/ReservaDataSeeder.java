package pe.edu.upeu.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pe.edu.upeu.entity.HistorialReserva;
import pe.edu.upeu.entity.Reserva;
import pe.edu.upeu.repository.ReservaRepository;

import java.time.LocalDate;

@ApplicationScoped
public class ReservaDataSeeder {

    @Inject
    ReservaRepository repository;

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (repository.count() > 0) {
            return;
        }

        Reserva reserva = new Reserva();
        reserva.setNombreUsuario("Carlos Medina");
        reserva.setIdLibro(2L);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaExpiracion(LocalDate.now().plusDays(3));
        reserva.setEstado("PENDIENTE");

        HistorialReserva historial = new HistorialReserva();
        historial.setEstadoNuevo("PENDIENTE");
        historial.setObservacion("Reserva creada");
        reserva.addHistorial(historial);

        repository.persist(reserva);
    }
}
