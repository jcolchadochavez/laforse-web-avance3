package pe.laforse.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.laforse.web.model.Servicio;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    List<Servicio> findAllByOrderByIdAsc();
}
