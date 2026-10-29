package pe.laforse.web.model;

/** Herramienta forense mostrada en la vista informativa (contenido estático, no se guarda en BD). */
public class Herramienta {
    private final String nombre;
    private final String fabricante;
    private final String categoria;
    private final String descripcion;
    private final String icono;

    public Herramienta(String nombre, String fabricante, String categoria, String descripcion, String icono) {
        this.nombre = nombre;
        this.fabricante = fabricante;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.icono = icono;
    }

    public String getNombre() { return nombre; }
    public String getFabricante() { return fabricante; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public String getIcono() { return icono; }
}
