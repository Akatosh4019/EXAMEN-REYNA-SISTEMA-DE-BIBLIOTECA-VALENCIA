package pe.edu.upeu.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.edu.upeu.entity.Categoria;

@ApplicationScoped
public class CategoriaRepository implements PanacheRepository<Categoria> {
}
