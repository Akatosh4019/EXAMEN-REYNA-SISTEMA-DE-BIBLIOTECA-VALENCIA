package pe.edu.upeu.services;

import pe.edu.upeu.entity.Libro;
import java.util.List;

public interface LibroService {
    Libro create(Libro libro);
    Libro update(Long id, Libro libro);
    void delete(Long id);
    Libro findById(Long id);
    List<Libro> findAll();
    Libro findByIsbn(String isbn);
    Integer consultarStock(Long id);
    Libro aumentarStock(Long id, int cantidad);
    Libro disminuirStock(Long id, int cantidad);
}
