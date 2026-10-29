package pe.laforse.web.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.laforse.web.exception.RecursoNoEncontradoException;
import pe.laforse.web.exception.ReglaNegocioException;
import pe.laforse.web.model.Perito;
import pe.laforse.web.repository.CasoRepository;
import pe.laforse.web.repository.PeritoRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** CRUD de peritos con sus reglas de negocio. */
@Service
@Transactional
public class PeritoService {

    private final PeritoRepository repo;
    private final CasoRepository casoRepo;

    public PeritoService(PeritoRepository repo, CasoRepository casoRepo) {
        this.repo = repo;
        this.casoRepo = casoRepo;
    }

    @Transactional(readOnly = true)
    public List<Perito> listar() {
        return repo.findAllByOrderByNombresAsc();
    }

    @Transactional(readOnly = true)
    public List<Perito> listarActivos() {
        return repo.findByActivoTrueOrderByNombresAsc();
    }

    @Transactional(readOnly = true)
    public Perito obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Perito no encontrado: " + id));
    }

    /** Cantidad de casos por perito, para mostrar en el listado. */
    @Transactional(readOnly = true)
    public Map<Long, Long> casosPorPerito() {
        return repo.findAll().stream()
            .collect(Collectors.toMap(Perito::getId, p -> casoRepo.countByPeritoId(p.getId())));
    }

    /** Registra o actualiza. Regla: el correo no se puede repetir entre peritos. */
    public Perito guardar(Perito perito) {
        String correo = perito.getCorreo().trim().toLowerCase();
        perito.setCorreo(correo);
        boolean duplicado = perito.getId() == null
            ? repo.existsByCorreoIgnoreCase(correo)
            : repo.existsByCorreoIgnoreCaseAndIdNot(correo, perito.getId());
        if (duplicado) {
            throw new ReglaNegocioException("correo", "Ya existe un perito registrado con ese correo");
        }
        if (perito.getId() != null) {
            obtener(perito.getId()); // valida que exista
        }
        return repo.save(perito);
    }

    /** Regla: no se elimina un perito que tiene casos asignados (se debe desactivar). */
    public void eliminar(Long id) {
        Perito perito = obtener(id);
        if (casoRepo.existsByPeritoId(id)) {
            throw new ReglaNegocioException("No se puede eliminar a " + perito.getNombres()
                + " porque tiene casos asignados. Desactívelo para que no reciba casos nuevos.");
        }
        repo.delete(perito);
    }
}
