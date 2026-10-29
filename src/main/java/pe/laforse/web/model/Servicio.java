package pe.laforse.web.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;

/** Servicio del catálogo (tabla servicio). Herramientas y entregables se guardan separados por ";". */
@Entity
@Table(name = "servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String resumen;

    @Column(nullable = false, length = 600)
    private String descripcion;

    @Column(name = "herramientas", length = 300)
    private String herramientasTexto;

    @Column(name = "entregables", length = 300)
    private String entregablesTexto;

    @Column(length = 60)
    private String plazo;

    @Column(length = 40)
    private String icono;

    @Column(nullable = false)
    private boolean disponible = true;

    protected Servicio() { }

    public Servicio(String nombre, String resumen, String descripcion, List<String> herramientas,
                    List<String> entregables, String plazo, String icono, boolean disponible) {
        this.nombre = nombre;
        this.resumen = resumen;
        this.descripcion = descripcion;
        this.herramientasTexto = String.join(";", herramientas);
        this.entregablesTexto = String.join(";", entregables);
        this.plazo = plazo;
        this.icono = icono;
        this.disponible = disponible;
    }

    private static List<String> separar(String texto) {
        if (texto == null || texto.isBlank()) return List.of();
        return Arrays.stream(texto.split(";")).map(String::trim).toList();
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getResumen() { return resumen; }
    public String getDescripcion() { return descripcion; }
    public List<String> getHerramientas() { return separar(herramientasTexto); }
    public List<String> getEntregables() { return separar(entregablesTexto); }
    public String getPlazo() { return plazo; }
    public String getIcono() { return icono; }
    public boolean isDisponible() { return disponible; }
}
