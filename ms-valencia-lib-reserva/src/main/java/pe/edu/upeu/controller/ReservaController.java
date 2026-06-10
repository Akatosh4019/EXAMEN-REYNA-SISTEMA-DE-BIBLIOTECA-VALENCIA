package pe.edu.upeu.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pe.edu.upeu.entity.Reserva;
import pe.edu.upeu.services.ReservaService;

import java.util.List;

@Path("/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaController {

    @Inject
    ReservaService service;

    @GET
    public List<Reserva> list() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Reserva get(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @POST
    public Reserva create(Reserva reserva) {
        return service.create(reserva);
    }

    @PUT
    @Path("/{id}")
    public Reserva update(@PathParam("id") Long id, Reserva reserva) {
        return service.update(id, reserva);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }

    @PUT
    @Path("/{id}/confirmar")
    public Reserva confirmar(@PathParam("id") Long id) {
        return service.confirmar(id);
    }

    @PUT
    @Path("/{id}/cancelar")
    public Reserva cancelar(@PathParam("id") Long id) {
        return service.cancelar(id);
    }

    @PUT
    @Path("/{id}/expirar")
    public Reserva expirar(@PathParam("id") Long id) {
        return service.expirar(id);
    }

    @PUT
    @Path("/{id}/historial/{estadoNuevo}")
    public Reserva registrarHistorial(@PathParam("id") Long id, @PathParam("estadoNuevo") String estadoNuevo, String observacion) {
        return service.registrarHistorial(id, estadoNuevo, observacion);
    }
}
