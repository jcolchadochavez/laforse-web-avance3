package pe.laforse.web.dto;

import pe.laforse.web.model.Caso;

import java.time.LocalDate;

/** Representación JSON de un caso para el API REST (evita exponer las entidades completas). */
public record CasoResponse(Long id, String expediente, String tipo, LocalDate fechaInicio, String estado,
                           Long peritoId, String perito, Integer servicioId, String servicio,
                           String solicitud, String observaciones) {

    public static CasoResponse de(Caso c) {
        return new CasoResponse(c.getId(), c.getExpediente(), c.getTipo(), c.getFechaInicio(), c.getEstado().name(),
            c.getPerito().getId(), c.getPerito().getNombres(), c.getServicio().getId(), c.getServicio().getNombre(),
            c.getSolicitud() == null ? null : c.getSolicitud().getCodigo(), c.getObservaciones());
    }
}
