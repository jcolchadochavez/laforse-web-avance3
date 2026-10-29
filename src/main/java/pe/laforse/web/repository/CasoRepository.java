package pe.laforse.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.laforse.web.model.Caso;
import pe.laforse.web.model.EstadoCaso;

import java.util.List;

public interface CasoRepository extends JpaRepository<Caso, Long> {

    /** Búsqueda combinada: estado opcional y texto opcional en expediente, tipo o perito. */
    @Query("""
           SELECT c FROM Caso c
           WHERE (:estado IS NULL OR c.estado = :estado)
             AND (:texto IS NULL
                  OR LOWER(c.expediente) LIKE LOWER(CONCAT('%', :texto, '%'))
                  OR LOWER(c.tipo) LIKE LOWER(CONCAT('%', :texto, '%'))
                  OR LOWER(c.perito.nombres) LIKE LOWER(CONCAT('%', :texto, '%')))
           ORDER BY c.fechaInicio DESC, c.id DESC
           """)
    List<Caso> buscar(@Param("estado") EstadoCaso estado, @Param("texto") String texto);

    long countByEstado(EstadoCaso estado);
    boolean existsByExpediente(String expediente);
    boolean existsByExpedienteAndIdNot(String expediente, Long id);
    boolean existsByPeritoId(Long peritoId);
    long countByPeritoId(Long peritoId);
    boolean existsBySolicitudId(Long solicitudId);

    /** Mayor número de expediente del año (los números tienen ancho fijo, por eso MAX funciona). */
    @Query("SELECT MAX(c.expediente) FROM Caso c WHERE c.expediente LIKE CONCAT(:prefijo, '%')")
    String maxExpediente(@Param("prefijo") String prefijo);
}
