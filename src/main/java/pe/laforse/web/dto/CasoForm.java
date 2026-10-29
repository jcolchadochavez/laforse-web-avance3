package pe.laforse.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import pe.laforse.web.model.EstadoCaso;

import java.time.LocalDate;

/** Datos que llegan del formulario de casos (vista Thymeleaf) y del API REST. */
public class CasoForm {

    @NotBlank(message = "Ingrese el número de expediente")
    @Pattern(regexp = "^PJ-\\d{4}-\\d{4}$", message = "Formato: PJ-AAAA-NNNN (ej. PJ-2026-0201)")
    private String expediente;

    @NotBlank(message = "Ingrese el tipo de peritaje")
    @Size(min = 5, max = 120, message = "El tipo debe tener entre 5 y 120 caracteres")
    private String tipo;

    @NotNull(message = "Ingrese la fecha de inicio")
    @PastOrPresent(message = "La fecha de inicio no puede ser futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @NotNull(message = "Seleccione el estado")
    private EstadoCaso estado;

    @NotNull(message = "Seleccione el perito responsable")
    private Long peritoId;

    @NotNull(message = "Seleccione el servicio")
    private Integer servicioId;

    /** Opcional: solicitud que originó el caso. */
    private Long solicitudId;

    @Size(max = 500, message = "Las observaciones admiten hasta 500 caracteres")
    private String observaciones;

    public String getExpediente() { return expediente; }
    public void setExpediente(String expediente) { this.expediente = expediente == null ? null : expediente.trim().toUpperCase(); }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public EstadoCaso getEstado() { return estado; }
    public void setEstado(EstadoCaso estado) { this.estado = estado; }
    public Long getPeritoId() { return peritoId; }
    public void setPeritoId(Long peritoId) { this.peritoId = peritoId; }
    public Integer getServicioId() { return servicioId; }
    public void setServicioId(Integer servicioId) { this.servicioId = servicioId; }
    public Long getSolicitudId() { return solicitudId; }
    public void setSolicitudId(Long solicitudId) { this.solicitudId = solicitudId; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
