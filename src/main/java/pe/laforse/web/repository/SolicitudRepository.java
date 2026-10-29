package pe.laforse.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.laforse.web.model.EstadoSolicitud;
import pe.laforse.web.model.Solicitud;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findAllByOrderByFechaDesc();
    List<Solicitud> findByEstadoOrderByFechaDesc(EstadoSolicitud estado);
    long countByEstado(EstadoSolicitud estado);
    long countByCodigoStartingWith(String prefijo);
    boolean existsByCodigo(String codigo);
}
