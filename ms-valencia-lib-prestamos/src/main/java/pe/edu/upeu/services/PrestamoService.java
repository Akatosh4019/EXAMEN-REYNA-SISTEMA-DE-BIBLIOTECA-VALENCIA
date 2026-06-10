package pe.edu.upeu.services;

import pe.edu.upeu.entity.Devolucion;
import pe.edu.upeu.entity.Prestamo;
import java.util.List;

public interface PrestamoService {
    Prestamo create(Prestamo prestamo);
    List<Prestamo> findAll();
    Prestamo findById(Long id);
    Prestamo update(Long id, Prestamo prestamo);
    void delete(Long id);
    Devolucion registrarDevolucion(Long id, Devolucion devolucion);
    Prestamo cambiarEstado(Long id, String estado);
}
