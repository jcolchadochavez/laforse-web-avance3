package pe.laforse.web.model;

/** Estado de un expediente pericial, con el color e ícono que usa la vista. */
public enum EstadoCaso {
    DICTAMEN_ENTREGADO("Dictamen entregado", "success", "bi-check-circle-fill"),
    EN_ANALISIS("En análisis", "warning", "bi-hourglass-split"),
    URGENTE("Urgente", "danger", "bi-exclamation-circle-fill"),
    PROGRAMADO("Programado", "secondary", "bi-clock");

    private final String etiqueta;
    private final String color;
    private final String icono;

    EstadoCaso(String etiqueta, String color, String icono) {
        this.etiqueta = etiqueta;
        this.color = color;
        this.icono = icono;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getColor() { return color; }
    public String getIcono() { return icono; }
}
