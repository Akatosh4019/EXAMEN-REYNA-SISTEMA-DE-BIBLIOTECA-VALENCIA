package pe.edu.upeu.controller;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pe.edu.upeu.entity.Autor;
import pe.edu.upeu.entity.Categoria;
import pe.edu.upeu.entity.Libro;
import pe.edu.upeu.errors.BadRequestException;
import pe.edu.upeu.errors.NotFoundException;
import pe.edu.upeu.repository.AutorRepository;
import pe.edu.upeu.repository.CategoriaRepository;
import pe.edu.upeu.services.LibroService;

import java.util.List;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LibroController {

    @Inject
    LibroService libroService;

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    AutorRepository autorRepository;

    @GET
    @Path("/libros")
    public List<Libro> listLibros() {
        return libroService.findAll();
    }

    @GET
    @Path("/libros/{id}")
    public Libro getLibro(@PathParam("id") Long id) {
        return libroService.findById(id);
    }

    @POST
    @Path("/libros")
    public Libro createLibro(Libro libro) {
        return libroService.create(libro);
    }

    @PUT
    @Path("/libros/{id}")
    public Libro updateLibro(@PathParam("id") Long id, Libro libro) {
        return libroService.update(id, libro);
    }

    @DELETE
    @Path("/libros/{id}")
    public void deleteLibro(@PathParam("id") Long id) {
        libroService.delete(id);
    }

    @GET
    @Path("/libros/isbn/{isbn}")
    public Libro getLibroByIsbn(@PathParam("isbn") String isbn) {
        return libroService.findByIsbn(isbn);
    }

    @GET
    @Path("/libros/{id}/stock")
    public Map<String, Integer> consultarStock(@PathParam("id") Long id) {
        return Map.of("stockDisponible", libroService.consultarStock(id));
    }

    @PUT
    @Path("/libros/{id}/stock/aumentar/{cantidad}")
    public Libro aumentarStock(@PathParam("id") Long id, @PathParam("cantidad") int cantidad) {
        return libroService.aumentarStock(id, cantidad);
    }

    @PUT
    @Path("/libros/{id}/stock/disminuir/{cantidad}")
    public Libro disminuirStock(@PathParam("id") Long id, @PathParam("cantidad") int cantidad) {
        return libroService.disminuirStock(id, cantidad);
    }

    @GET
    @Path("/categorias")
    public List<Categoria> listCategorias() {
        return categoriaRepository.listAll();
    }

    @POST
    @Path("/categorias")
    @Transactional
    public Categoria createCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new BadRequestException("El nombre de la categoria es obligatorio");
        }
        categoriaRepository.persist(categoria);
        return categoria;
    }

    @PUT
    @Path("/categorias/{id}")
    @Transactional
    public Categoria updateCategoria(@PathParam("id") Long id, Categoria categoria) {
        Categoria entity = categoriaRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Categoria no encontrada con id: " + id);
        }
        entity.setNombre(categoria.getNombre());
        entity.setDescripcion(categoria.getDescripcion());
        return entity;
    }

    @DELETE
    @Path("/categorias/{id}")
    @Transactional
    public void deleteCategoria(@PathParam("id") Long id) {
        if (!categoriaRepository.deleteById(id)) {
            throw new NotFoundException("Categoria no encontrada con id: " + id);
        }
    }

    @GET
    @Path("/autores")
    public List<Autor> listAutores() {
        return autorRepository.listAll();
    }

    @POST
    @Path("/autores")
    @Transactional
    public Autor createAutor(Autor autor) {
        if (autor.getNombres() == null || autor.getNombres().isBlank()) {
            throw new BadRequestException("Los nombres del autor son obligatorios");
        }
        if (autor.getApellidos() == null || autor.getApellidos().isBlank()) {
            throw new BadRequestException("Los apellidos del autor son obligatorios");
        }
        autorRepository.persist(autor);
        return autor;
    }

    @PUT
    @Path("/autores/{id}")
    @Transactional
    public Autor updateAutor(@PathParam("id") Long id, Autor autor) {
        Autor entity = autorRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Autor no encontrado con id: " + id);
        }
        entity.setNombres(autor.getNombres());
        entity.setApellidos(autor.getApellidos());
        entity.setNacionalidad(autor.getNacionalidad());
        return entity;
    }

    @DELETE
    @Path("/autores/{id}")
    @Transactional
    public void deleteAutor(@PathParam("id") Long id) {
        if (!autorRepository.deleteById(id)) {
            throw new NotFoundException("Autor no encontrado con id: " + id);
        }
    }
}
