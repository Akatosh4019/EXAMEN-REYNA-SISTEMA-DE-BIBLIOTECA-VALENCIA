package pe.edu.upeu.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pe.edu.upeu.entity.Devolucion;
import pe.edu.upeu.entity.Prestamo;
import pe.edu.upeu.services.PrestamoService;

import java.util.List;

@Path("/prestamos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrestamoController {

    @Inject
    PrestamoService service;

    @GET
    public List<Prestamo> list() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Prestamo get(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @POST
    public Prestamo create(Prestamo prestamo) {
        return service.create(prestamo);
    }

    @PUT
    @Path("/{id}")
    public Prestamo update(@PathParam("id") Long id, Prestamo prestamo) {
        return service.update(id, prestamo);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }

    @POST
    @Path("/{id}/devolucion")
    public Devolucion registrarDevolucion(@PathParam("id") Long id, Devolucion devolucion) {
        return service.registrarDevolucion(id, devolucion);
    }

    @PUT
    @Path("/{id}/estado/{estado}")
    public Prestamo cambiarEstado(@PathParam("id") Long id, @PathParam("estado") String estado) {
        return service.cambiarEstado(id, estado);
    }
}
