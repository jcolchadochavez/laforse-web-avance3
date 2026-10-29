package pe.laforse.web.service;

import org.springframework.stereotype.Service;
import pe.laforse.web.model.Empresa;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpresaService {

    private final Empresa empresa;

    public EmpresaService() {
        Map<String, List<String>> organigrama = new LinkedHashMap<>();
        organigrama.put("Dirección Técnica (Peritajes)", List.of("Peritos informáticos", "Analistas forenses", "Soporte de laboratorio"));
        organigrama.put("Dirección Legal y Cumplimiento", List.of("Asesoría legal", "Cumplimiento normativo"));
        organigrama.put("Dirección Administrativa y Comercial", List.of("Atención al cliente", "Administración y finanzas"));

        this.empresa = new Empresa(
            "LAFORSE-HG",
            "Servicios de peritaje informático, informática forense y ciberseguridad",
            "Brindar servicios de análisis forense digital y ciberseguridad que permitan a nuestros clientes sustentar decisiones legales y corporativas sobre evidencia digital confiable, trazable y obtenida bajo estándares técnicos y normativos vigentes.",
            "Ser la empresa peruana de referencia en peritaje informático y respuesta a incidentes digitales, reconocida por la rigurosidad técnica y la integridad de la cadena de custodia en cada caso atendido.",
            List.of(
                "Consolidar un laboratorio forense con procesos certificados de cadena de custodia.",
                "Reducir los tiempos de respuesta ante incidentes de seguridad a menos de 24 horas.",
                "Ampliar la cobertura de servicios periciales a nivel nacional mediante atención remota y presencial.",
                "Formar y capacitar continuamente al equipo técnico en normativa y herramientas forenses actualizadas."
            ),
            organigrama
        );
    }

    public Empresa obtener() {
        return empresa;
    }
}
