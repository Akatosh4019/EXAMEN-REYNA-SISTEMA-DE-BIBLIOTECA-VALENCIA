package pe.edu.upeu.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pe.edu.upeu.entity.DetallePrestamo;
import pe.edu.upeu.entity.Prestamo;
import pe.edu.upeu.repository.PrestamoRepository;

import java.time.LocalDate;

@ApplicationScoped
public class PrestamoDataSeeder {

    @Inject
    PrestamoRepository repository;

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (repository.count() > 0) {
            return;
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setNombreUsuario("Mariana Rojas");
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaVencimiento(LocalDate.now().plusDays(7));
        prestamo.setEstado("ACTIVO");

        DetallePrestamo detalle = new DetallePrestamo();
        detalle.setIdLibro(1L);
        detalle.setCantidad(1);
        prestamo.addDetalle(detalle);

        repository.persist(prestamo);
    }
}
