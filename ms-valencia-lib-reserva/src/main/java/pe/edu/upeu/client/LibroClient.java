package pe.edu.upeu.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import pe.edu.upeu.dto.LibroDTO;

@Path("/libros")
@RegisterRestClient(configKey = "libros-api")
public interface LibroClient {

    @GET
    @Path("/{id}")
    LibroDTO buscarLibroPorId(@PathParam("id") Long id);
}
