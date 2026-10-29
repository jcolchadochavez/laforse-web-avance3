package pe.laforse.web.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.laforse.web.dto.CasoForm;
import pe.laforse.web.exception.RecursoNoEncontradoException;
import pe.laforse.web.exception.ReglaNegocioException;
import pe.laforse.web.model.*;
import pe.laforse.web.repository.CasoRepository;

import java.time.LocalDate;
import java.time.Year;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** CRUD de casos (expedientes) con las reglas de negocio del laboratorio. */
@Service
@Transactional
public class CasoService {

    private final CasoRepository repo;
    private final PeritoService peritoService;
    private final ServicioService servicioService;
    private final SolicitudService solicitudService;

    public CasoService(CasoRepository repo, PeritoService peritoService,
                       ServicioService servicioService, SolicitudService solicitudService) {
        this.repo = repo;
        this.peritoService = peritoService;
        this.servicioService = servicioService;
        this.solicitudService = solicitudService;
    }

    @Transactional(readOnly = true)
    public List<Caso> listar(EstadoCaso estado, String texto) {
        String t = (texto == null || texto.isBlank()) ? null : texto.trim();
        return repo.buscar(estado, t);
    }

    @Transactional(readOnly = true)
    public List<Caso> listar(EstadoCaso estado) {
        return listar(estado, null);
    }

    @Transactional(readOnly = true)
    public Caso obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Caso no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Map<EstadoCaso, Long> contarPorEstado() {
        Map<EstadoCaso, Long> conteo = new EnumMap<>(EstadoCaso.class);
        for (EstadoCaso e : EstadoCaso.values()) conteo.put(e, repo.countByEstado(e));
        return conteo;
    }

    @Transactional(readOnly = true)
    public long total() {
        return repo.count();
    }

    /** Sugiere el siguiente número de expediente del año: PJ-AAAA-NNNN. */
    @Transactional(readOnly = true)
    public String siguienteExpediente() {
        String prefijo = "PJ-" + Year.now().getValue() + "-";
        String mayor = repo.maxExpediente(prefijo);
        int n = mayor == null ? 1 : Integer.parseInt(mayor.substring(prefijo.length())) + 1;
        return prefijo + String.format("%04d", n);
    }

    /** Formulario vacío; si viene de una solicitud, copia servicio y urgencia. */
    @Transactional(readOnly = true)
    public CasoForm nuevoFormulario(Long solicitudId) {
        CasoForm f = new CasoForm();
        f.setExpediente(siguienteExpediente());
        f.setFechaInicio(LocalDate.now());
        f.setEstado(EstadoCaso.PROGRAMADO);
        if (solicitudId != null) {
            Solicitud s = solicitudService.obtener(solicitudId);
            f.setSolicitudId(s.getId());
            f.setServicioId(s.getServicio().getId());
            f.setTipo(s.getServicio().getNombre() + " – " + s.getNombre());
            f.setObservaciones("Generado desde la solicitud " + s.getCodigo() + ". " + s.getMensaje());
            if (f.getObservaciones().length() > 500) f.setObservaciones(f.getObservaciones().substring(0, 500));
            // Regla: una solicitud con urgencia crítica abre el caso como URGENTE
            if (s.getUrgencia() == Urgencia.CRITICA) f.setEstado(EstadoCaso.URGENTE);
        }
        return f;
    }

    @Transactional(readOnly = true)
    public CasoForm aFormulario(Caso c) {
        CasoForm f = new CasoForm();
        f.setExpediente(c.getExpediente());
        f.setTipo(c.getTipo());
        f.setFechaInicio(c.getFechaInicio());
        f.setEstado(c.getEstado());
        f.setPeritoId(c.getPerito().getId());
        f.setServicioId(c.getServicio().getId());
        f.setSolicitudId(c.getSolicitud() == null ? null : c.getSolicitud().getId());
        f.setObservaciones(c.getObservaciones());
        return f;
    }

    public Caso registrar(CasoForm form) {
        if (repo.existsByExpediente(form.getExpediente())) {
            throw new ReglaNegocioException("expediente", "El expediente " + form.getExpediente() + " ya está registrado");
        }
        Caso caso = new Caso();
        copiar(form, caso, true);
        Caso guardado = repo.save(caso);
        // Integración: la solicitud de origen pasa a "Atendida"
        if (guardado.getSolicitud() != null) {
            solicitudService.cambiarEstado(guardado.getSolicitud().getId(), EstadoSolicitud.ATENDIDA);
        }
        return guardado;
    }

    public Caso actualizar(Long id, CasoForm form) {
        Caso caso = obtener(id);
        if (caso.isCerrado()) {
            throw new ReglaNegocioException("El caso " + caso.getExpediente()
                + " tiene el dictamen entregado y ya no se puede modificar.");
        }
        if (repo.existsByExpedienteAndIdNot(form.getExpediente(), id)) {
            throw new ReglaNegocioException("expediente", "El expediente " + form.getExpediente() + " ya está registrado");
        }
        boolean cambiaPerito = !caso.getPerito().getId().equals(form.getPeritoId());
        copiar(form, caso, cambiaPerito);
        return repo.save(caso);
    }

    /** Regla: un caso con dictamen entregado se conserva por trazabilidad y no se elimina. */
    public void eliminar(Long id) {
        Caso caso = obtener(id);
        if (caso.isCerrado()) {
            throw new ReglaNegocioException("El caso " + caso.getExpediente()
                + " tiene el dictamen entregado y se conserva por trazabilidad.");
        }
        repo.delete(caso);
    }

    private void copiar(CasoForm form, Caso caso, boolean validarPeritoActivo) {
        Perito perito = peritoService.obtener(form.getPeritoId());
        // Regla: solo se asignan casos a peritos activos
        if (validarPeritoActivo && !perito.isActivo()) {
            throw new ReglaNegocioException("peritoId", "El perito " + perito.getNombres() + " está inactivo");
        }
        Servicio servicio = servicioService.buscarPorId(form.getServicioId())
            .orElseThrow(() -> new ReglaNegocioException("servicioId", "El servicio seleccionado no existe"));
        Solicitud solicitud = form.getSolicitudId() == null ? null : solicitudService.obtener(form.getSolicitudId());

        caso.setExpediente(form.getExpediente());
        caso.setTipo(form.getTipo().trim());
        caso.setFechaInicio(form.getFechaInicio());
        caso.setEstado(form.getEstado());
        caso.setPerito(perito);
        caso.setServicio(servicio);
        caso.setSolicitud(solicitud);
        caso.setObservaciones(form.getObservaciones() == null || form.getObservaciones().isBlank()
            ? null : form.getObservaciones().trim());
    }
}
