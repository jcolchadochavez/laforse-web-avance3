package pe.laforse.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.laforse.web.model.Perito;

import java.util.List;

public interface PeritoRepository extends JpaRepository<Perito, Long> {
    List<Perito> findAllByOrderByNombresAsc();
    List<Perito> findByActivoTrueOrderByNombresAsc();
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);
}
