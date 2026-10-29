package pe.laforse.web.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Solicitud de peritaje enviada desde el formulario de contacto (tabla solicitud). */
@Entity
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(length = 9)
    private String telefono;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Urgencia urgencia;

    @Column(nullable = false, length = 600)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    protected Solicitud() { }

    public Solicitud(String codigo, LocalDateTime fecha, String nombre, String correo, String telefono,
                     Servicio servicio, Urgencia urgencia, String mensaje) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.servicio = servicio;
        this.urgencia = urgencia;
        this.mensaje = mensaje;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public LocalDateTime getFecha() { return fecha; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public Servicio getServicio() { return servicio; }
    public Urgencia getUrgencia() { return urgencia; }
    public String getMensaje() { return mensaje; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
}
