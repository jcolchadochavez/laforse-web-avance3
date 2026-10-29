package pe.laforse.web.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.laforse.web.exception.RecursoNoEncontradoException;
import pe.laforse.web.model.Servicio;
import pe.laforse.web.repository.ServicioRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ServicioService {

    private final ServicioRepository repo;

    public ServicioService(ServicioRepository repo) {
        this.repo = repo;
    }

    public List<Servicio> listar() {
        return repo.findAllByOrderByIdAsc();
    }

    public Optional<Servicio> buscarPorId(Integer id) {
        return id == null ? Optional.empty() : repo.findById(id);
    }

    public Servicio obtener(Integer id) {
        return buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado: " + id));
    }
}
