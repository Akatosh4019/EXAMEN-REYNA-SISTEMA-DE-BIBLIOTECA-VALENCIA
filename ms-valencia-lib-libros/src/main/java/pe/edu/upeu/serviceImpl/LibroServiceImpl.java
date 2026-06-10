package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pe.edu.upeu.entity.Autor;
import pe.edu.upeu.entity.Categoria;
import pe.edu.upeu.entity.Libro;
import pe.edu.upeu.errors.*;
import pe.edu.upeu.repository.AutorRepository;
import pe.edu.upeu.repository.CategoriaRepository;
import pe.edu.upeu.repository.LibroRepository;
import pe.edu.upeu.services.LibroService;

import java.util.List;

@ApplicationScoped
public class LibroServiceImpl implements LibroService {

    @Inject
    LibroRepository libroRepository;

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    AutorRepository autorRepository;

    @Override
    @Transactional
    public Libro create(Libro libro) {
        validarLibro(libro);

        if (libroRepository.find("isbn", libro.getIsbn()).firstResult() != null) {
            throw new ConflictException("Ya existe un libro con ISBN: " + libro.getIsbn());
        }

        asignarRelaciones(libro);
        if (libro.getEstado() == null || libro.getEstado().isBlank()) {
            libro.setEstado("DISPONIBLE");
        }

        libroRepository.persist(libro);
        return libro;
    }

    @Override
    @Transactional
    public Libro update(Long id, Libro libro) {
        validarLibro(libro);
        Libro entity = findById(id);

        Libro duplicado = libroRepository.find("isbn", libro.getIsbn()).firstResult();
        if (duplicado != null && !duplicado.getIdLibro().equals(id)) {
            throw new ConflictException("Ya existe un libro con ISBN: " + libro.getIsbn());
        }

        entity.setTitulo(libro.getTitulo());
        entity.setIsbn(libro.getIsbn());
        entity.setAnioPublicacion(libro.getAnioPublicacion());
        entity.setEstado(libro.getEstado());
        entity.setStockTotal(libro.getStockTotal());
        entity.setStockDisponible(libro.getStockDisponible());
        entity.setCategoria(obtenerCategoria(libro));
        entity.setAutor(obtenerAutor(libro));
        return entity;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Libro libro = findById(id);
        libroRepository.delete(libro);
    }

    @Override
    public Libro findById(Long id) {
        Libro libro = libroRepository.findById(id);
        if (libro == null) {
            throw new NotFoundException("Libro no encontrado con id: " + id);
        }
        return libro;
    }

    @Override
    public List<Libro> findAll() {
        return libroRepository.listAll();
    }

    @Override
    public Libro findByIsbn(String isbn) {
        Libro libro = libroRepository.find("isbn", isbn).firstResult();
        if (libro == null) {
            throw new NotFoundException("Libro no encontrado con ISBN: " + isbn);
        }
        return libro;
    }

    @Override
    public Integer consultarStock(Long id) {
        return findById(id).getStockDisponible();
    }

    @Override
    @Transactional
    public Libro aumentarStock(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }
        Libro libro = findById(id);
        libro.setStockTotal(libro.getStockTotal() + cantidad);
        libro.setStockDisponible(libro.getStockDisponible() + cantidad);
        return libro;
    }

    @Override
    @Transactional
    public Libro disminuirStock(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }
        Libro libro = findById(id);
        if (!"DISPONIBLE".equals(libro.getEstado())) {
            throw new BadRequestException("El libro no esta disponible");
        }
        if (libro.getStockDisponible() < cantidad) {
            throw new BadRequestException("No hay stock disponible suficiente");
        }
        libro.setStockDisponible(libro.getStockDisponible() - cantidad);
        return libro;
    }

    private void validarLibro(Libro libro) {
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new BadRequestException("El titulo es obligatorio");
        }
        if (libro.getIsbn() == null || libro.getIsbn().isBlank()) {
            throw new BadRequestException("El ISBN es obligatorio");
        }
        if (libro.getStockTotal() == null || libro.getStockTotal() < 0) {
            throw new BadRequestException("El stock total no puede ser negativo");
        }
        if (libro.getStockDisponible() == null || libro.getStockDisponible() < 0) {
            throw new BadRequestException("El stock disponible no puede ser negativo");
        }
        if (libro.getStockDisponible() > libro.getStockTotal()) {
            throw new BadRequestException("El stock disponible no puede superar el stock total");
        }
        obtenerCategoria(libro);
        obtenerAutor(libro);
    }

    private void asignarRelaciones(Libro libro) {
        libro.setCategoria(obtenerCategoria(libro));
        libro.setAutor(obtenerAutor(libro));
    }

    private Categoria obtenerCategoria(Libro libro) {
        Long id = libro.getCategoria() == null ? null : libro.getCategoria().getIdCategoria();
        if (id == null) {
            throw new BadRequestException("La categoria es obligatoria");
        }
        Categoria categoria = categoriaRepository.findById(id);
        if (categoria == null) {
            throw new NotFoundException("Categoria no encontrada con id: " + id);
        }
        return categoria;
    }

    private Autor obtenerAutor(Libro libro) {
        Long id = libro.getAutor() == null ? null : libro.getAutor().getIdAutor();
        if (id == null) {
            throw new BadRequestException("El autor es obligatorio");
        }
        Autor autor = autorRepository.findById(id);
        if (autor == null) {
            throw new NotFoundException("Autor no encontrado con id: " + id);
        }
        return autor;
    }
}
