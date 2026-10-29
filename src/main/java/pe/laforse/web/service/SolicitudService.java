package pe.laforse.web.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.laforse.web.exception.RecursoNoEncontradoException;
import pe.laforse.web.exception.ReglaNegocioException;
import pe.laforse.web.model.ContactoForm;
import pe.laforse.web.model.EstadoSolicitud;
import pe.laforse.web.model.Servicio;
import pe.laforse.web.model.Solicitud;
import pe.laforse.web.repository.CasoRepository;
import pe.laforse.web.repository.SolicitudRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Registro y gestión de las solicitudes que llegan por el formulario de contacto. */
@Service
@Transactional
public class SolicitudService {

    private final SolicitudRepository repo;
    private final CasoRepository casoRepo;
    private final ServicioService servicioService;

    public SolicitudService(SolicitudRepository repo, CasoRepository casoRepo, ServicioService servicioService) {
        this.repo = repo;
        this.casoRepo = casoRepo;
        this.servicioService = servicioService;
    }

    /** Guarda la solicitud en MySQL y genera el código correlativo SOL-AAAA-NNNN. */
    public Solicitud registrar(ContactoForm form) {
        Servicio servicio = servicioService.buscarPorId(form.getServicioId())
            .orElseThrow(() -> new ReglaNegocioException("servicioId", "El servicio seleccionado no existe"));
        String prefijo = "SOL-" + Year.now().getValue() + "-";
        long n = repo.countByCodigoStartingWith(prefijo) + 1;
        String codigo;
        do {
            codigo = prefijo + String.format("%04d", n++);
        } while (repo.existsByCodigo(codigo)); // evita repetir códigos si se eliminó alguna solicitud
        String telefono = form.getTelefono() == null || form.getTelefono().isBlank() ? null : form.getTelefono();
        Solicitud s = new Solicitud(codigo, LocalDateTime.now(), form.getNombre().trim(), form.getCorreo().trim().toLowerCase(),
            telefono, servicio, form.getUrgencia(), form.getMensaje().trim());
        return repo.save(s);
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listar(EstadoSolicitud estado) {
        return estado == null ? repo.findAllByOrderByFechaDesc() : repo.findByEstadoOrderByFechaDesc(estado);
    }

    @Transactional(readOnly = true)
    public Solicitud obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Map<EstadoSolicitud, Long> contarPorEstado() {
        Map<EstadoSolicitud, Long> conteo = new EnumMap<>(EstadoSolicitud.class);
        for (EstadoSolicitud e : EstadoSolicitud.values()) conteo.put(e, repo.countByEstado(e));
        return conteo;
    }

    @Transactional(readOnly = true)
    public long pendientes() {
        return repo.countByEstado(EstadoSolicitud.PENDIENTE);
    }

    public void cambiarEstado(Long id, EstadoSolicitud estado) {
        Solicitud s = obtener(id);
        s.setEstado(estado);
    }

    /** Regla: una solicitud que ya generó un caso no se elimina (se conserva la trazabilidad). */
    public void eliminar(Long id) {
        Solicitud s = obtener(id);
        if (casoRepo.existsBySolicitudId(id)) {
            throw new ReglaNegocioException("La solicitud " + s.getCodigo() + " ya generó un caso y no se puede eliminar.");
        }
        repo.delete(s);
    }
}
