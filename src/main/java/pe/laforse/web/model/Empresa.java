package pe.laforse.web.model;

import java.util.List;
import java.util.Map;

/** Datos institucionales que se muestran en la sección "Nosotros" (contenido estático, no se guarda en BD). */
public class Empresa {
    private final String nombre;
    private final String rubro;
    private final String mision;
    private final String vision;
    private final List<String> objetivos;
    private final Map<String, List<String>> organigrama;

    public Empresa(String nombre, String rubro, String mision, String vision,
                   List<String> objetivos, Map<String, List<String>> organigrama) {
        this.nombre = nombre;
        this.rubro = rubro;
        this.mision = mision;
        this.vision = vision;
        this.objetivos = objetivos;
        this.organigrama = organigrama;
    }

    public String getNombre() { return nombre; }
    public String getRubro() { return rubro; }
    public String getMision() { return mision; }
    public String getVision() { return vision; }
    public List<String> getObjetivos() { return objetivos; }
    public Map<String, List<String>> getOrganigrama() { return organigrama; }
}
