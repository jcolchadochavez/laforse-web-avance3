package pe.laforse.web.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/** Expediente pericial (tabla caso). Se relaciona con perito, servicio y, opcionalmente, con la solicitud de origen. */
@Entity
@Table(name = "caso")
public class Caso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String expediente;

    @Column(nullable = false, length = 120)
    private String tipo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCaso estado;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(optional = false)
    @JoinColumn(name = "perito_id", nullable = false)
    private Perito perito;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    private Solicitud solicitud;

    public Caso() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExpediente() { return expediente; }
    public void setExpediente(String expediente) { this.expediente = expediente; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public EstadoCaso getEstado() { return estado; }
    public void setEstado(EstadoCaso estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Perito getPerito() { return perito; }
    public void setPerito(Perito perito) { this.perito = perito; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public Solicitud getSolicitud() { return solicitud; }
    public void setSolicitud(Solicitud solicitud) { this.solicitud = solicitud; }

    /** Regla de negocio: un caso con dictamen entregado queda cerrado. */
    public boolean isCerrado() { return estado == EstadoCaso.DICTAMEN_ENTREGADO; }
}
