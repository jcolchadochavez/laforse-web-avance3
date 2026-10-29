package pe.laforse.web.model;

public enum Urgencia {
    BAJA("Baja – más de 15 días"),
    MEDIA("Media – entre 7 y 15 días"),
    ALTA("Alta – menos de 7 días"),
    CRITICA("Crítica – incidente en curso");

    private final String etiqueta;

    Urgencia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() { return etiqueta; }
}
