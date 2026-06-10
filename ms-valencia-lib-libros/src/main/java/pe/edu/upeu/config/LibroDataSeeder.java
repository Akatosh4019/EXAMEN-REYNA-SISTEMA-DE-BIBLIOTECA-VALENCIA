package pe.edu.upeu.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pe.edu.upeu.entity.Autor;
import pe.edu.upeu.entity.Categoria;
import pe.edu.upeu.entity.Libro;
import pe.edu.upeu.repository.AutorRepository;
import pe.edu.upeu.repository.CategoriaRepository;
import pe.edu.upeu.repository.LibroRepository;

@ApplicationScoped
public class LibroDataSeeder {

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    AutorRepository autorRepository;

    @Inject
    LibroRepository libroRepository;

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (libroRepository.count() > 0) {
            return;
        }

        Categoria novela = crearCategoria("Novela", "Narrativa literaria");
        Categoria tecnologia = crearCategoria("Tecnologia", "Libros tecnicos y de computacion");
        Autor garcia = crearAutor("Gabriel", "Garcia Marquez", "Colombiana");
        Autor martin = crearAutor("Robert C.", "Martin", "Estadounidense");

        crearLibro("Cien anos de soledad", "9780307474728", 1967, "DISPONIBLE", 8, 8, novela, garcia);
        crearLibro("Clean Code", "9780132350884", 2008, "DISPONIBLE", 5, 5, tecnologia, martin);
    }

    private Categoria crearCategoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoriaRepository.persist(categoria);
        return categoria;
    }

    private Autor crearAutor(String nombres, String apellidos, String nacionalidad) {
        Autor autor = new Autor();
        autor.setNombres(nombres);
        autor.setApellidos(apellidos);
        autor.setNacionalidad(nacionalidad);
        autorRepository.persist(autor);
        return autor;
    }

    private void crearLibro(String titulo, String isbn, Integer anio, String estado, Integer stockTotal, Integer stockDisponible, Categoria categoria, Autor autor) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setIsbn(isbn);
        libro.setAnioPublicacion(anio);
        libro.setEstado(estado);
        libro.setStockTotal(stockTotal);
        libro.setStockDisponible(stockDisponible);
        libro.setCategoria(categoria);
        libro.setAutor(autor);
        libroRepository.persist(libro);
    }
}
