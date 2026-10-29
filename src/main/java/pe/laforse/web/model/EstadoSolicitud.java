package pe.laforse.web.model;

public enum EstadoSolicitud {
    PENDIENTE("Pendiente", "warning", "bi-inbox"),
    ATENDIDA("Atendida", "success", "bi-check2-circle"),
    DESCARTADA("Descartada", "secondary", "bi-x-circle");

    private final String etiqueta;
    private final String color;
    private final String icono;

    EstadoSolicitud(String etiqueta, String color, String icono) {
        this.etiqueta = etiqueta;
        this.color = color;
        this.icono = icono;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getColor() { return color; }
    public String getIcono() { return icono; }
}
