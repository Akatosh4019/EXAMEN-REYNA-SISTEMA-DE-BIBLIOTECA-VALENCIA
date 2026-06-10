package pe.edu.upeu.services;

import pe.edu.upeu.entity.Reserva;
import java.util.List;

public interface ReservaService {
    Reserva create(Reserva reserva);
    List<Reserva> findAll();
    Reserva findById(Long id);
    Reserva update(Long id, Reserva reserva);
    void delete(Long id);
    Reserva confirmar(Long id);
    Reserva cancelar(Long id);
    Reserva expirar(Long id);
    Reserva registrarHistorial(Long id, String estadoNuevo, String observacion);
}
