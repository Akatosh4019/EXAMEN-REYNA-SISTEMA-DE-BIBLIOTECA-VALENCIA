package pe.edu.upeu.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.edu.upeu.entity.Libro;

@ApplicationScoped
public class LibroRepository implements PanacheRepository<Libro> {
}
