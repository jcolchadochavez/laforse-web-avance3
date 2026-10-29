package pe.laforse.web.service;

import org.springframework.stereotype.Service;
import pe.laforse.web.model.Herramienta;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class HerramientaService {

    private final List<Herramienta> herramientas = List.of(
        new Herramienta("EnCase Forensic", "OpenText", "Análisis de cómputo",
            "Adquisición y análisis de discos con amplia aceptación en tribunales.", "bi-pc-display"),
        new Herramienta("FTK / FTK Imager", "Exterro (AccessData)", "Análisis de cómputo",
            "Indexación masiva de evidencia y creación de imágenes forenses verificadas.", "bi-device-hdd"),
        new Herramienta("Autopsy", "Basis Technology", "Análisis de cómputo",
            "Plataforma de código abierto para líneas de tiempo y recuperación de archivos.", "bi-search"),
        new Herramienta("Magnet AXIOM", "Magnet Forensics", "Análisis integral",
            "Correlaciona evidencia de computadoras, móviles y nube en un solo caso.", "bi-diagram-3"),
        new Herramienta("Cellebrite UFED", "Cellebrite", "Forense móvil",
            "Extracción lógica y física de miles de modelos de celulares.", "bi-phone"),
        new Herramienta("Oxygen Forensic", "Oxygen Forensics", "Forense móvil",
            "Análisis de aplicaciones de mensajería, nube y drones.", "bi-chat-dots"),
        new Herramienta("Wireshark", "Wireshark Foundation", "Redes e incidentes",
            "Captura y análisis de tráfico de red durante la respuesta a incidentes.", "bi-ethernet"),
        new Herramienta("Volatility", "Volatility Foundation", "Redes e incidentes",
            "Análisis de memoria RAM para detectar malware y procesos ocultos.", "bi-memory")
    );

    public List<Herramienta> listar() {
        return herramientas;
    }

    /** Agrupa las herramientas por categoría (ordenadas alfabéticamente) para las pestañas de la vista. */
    public Map<String, List<Herramienta>> agruparPorCategoria() {
        return herramientas.stream()
            .collect(Collectors.groupingBy(Herramienta::getCategoria, TreeMap::new, Collectors.toList()));
    }
}
