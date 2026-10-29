package pe.laforse.web.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.laforse.web.model.*;
import pe.laforse.web.repository.CasoRepository;
import pe.laforse.web.repository.PeritoRepository;
import pe.laforse.web.repository.ServicioRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Carga los datos iniciales solo si las tablas están vacías
 * (la primera vez que se ejecuta el sistema o si no se corrió el script SQL).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ServicioRepository servicios;
    private final PeritoRepository peritos;
    private final CasoRepository casos;

    public DataSeeder(ServicioRepository servicios, PeritoRepository peritos, CasoRepository casos) {
        this.servicios = servicios;
        this.peritos = peritos;
        this.casos = casos;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (servicios.count() == 0) {
            servicios.saveAll(List.of(
                new Servicio("Adquisición de evidencia",
                    "Copiado forense bit a bit de discos, dispositivos móviles y servidores, preservando el hash original.",
                    "Generamos imágenes forenses verificadas con hash MD5/SHA-256 y documentamos cada paso en el acta de cadena de custodia, de modo que la evidencia pueda ser presentada ante fiscalía o en juicio.",
                    List.of("FTK Imager", "EnCase", "Tableau TX1"),
                    List.of("Imagen forense verificada", "Acta de cadena de custodia", "Registro fotográfico"),
                    "24 a 72 horas", "bi-hdd-network", true),
                new Servicio("Análisis forense",
                    "Reconstrucción de líneas de tiempo, artefactos de sistema, mensajería y bases de datos.",
                    "Analizamos la imagen adquirida para reconstruir qué ocurrió, cuándo y quién intervino: archivos eliminados, navegación, registros del sistema, correos y documentos.",
                    List.of("Autopsy", "Magnet AXIOM", "EnCase"),
                    List.of("Línea de tiempo de eventos", "Informe de hallazgos", "Anexos técnicos"),
                    "5 a 15 días hábiles", "bi-search", true),
                new Servicio("Dictamen pericial",
                    "Informes técnico-legales alineados a normativa del Ministerio Público y estándares internacionales.",
                    "Elaboramos el dictamen pericial con lenguaje claro para jueces y abogados, sustentado en la metodología ISO/IEC 27037 y con disponibilidad del perito para su ratificación.",
                    List.of("Metodología ISO/IEC 27037", "Plantillas del Ministerio Público"),
                    List.of("Dictamen pericial firmado", "Ratificación en audiencia"),
                    "Según complejidad del caso", "bi-file-earmark-text", true),
                new Servicio("Respuesta a incidentes",
                    "Contención y análisis post-incidente ante intrusiones, ransomware y fuga de información.",
                    "Activamos un equipo de respuesta que contiene el incidente, preserva la evidencia volátil e identifica el vector de ataque para evitar que vuelva a ocurrir.",
                    List.of("Wireshark", "Velociraptor", "Volatility"),
                    List.of("Informe de incidente", "Indicadores de compromiso", "Plan de remediación"),
                    "Atención 24/7", "bi-shield-exclamation", true),
                new Servicio("Forense móvil",
                    "Extracción y análisis de dispositivos Android/iOS, aplicaciones de mensajería y metadatos.",
                    "Extraemos de forma lógica, física o de sistema de archivos la información de celulares y tablets, incluyendo WhatsApp, Telegram, fotos con geolocalización y registros de llamadas.",
                    List.of("Cellebrite UFED", "Oxygen Forensic", "XRY"),
                    List.of("Reporte de extracción", "Análisis de mensajería", "Mapa de ubicaciones"),
                    "3 a 10 días hábiles", "bi-phone", true),
                new Servicio("Auditorías internas",
                    "Investigaciones internas corporativas: fraude, mal uso de activos digitales y cumplimiento normativo.",
                    "Apoyamos a las áreas de auditoría y compliance en investigaciones sobre fraude, fuga de información o uso indebido de equipos, con reserva y respeto a la normativa laboral.",
                    List.of("Magnet AXIOM", "Metodología propia"),
                    List.of("Informe de auditoría", "Recomendaciones de control"),
                    "Bajo demanda", "bi-clipboard-data", false)
            ));
        }
        if (peritos.count() == 0) {
            peritos.saveAll(List.of(
                new Perito("Jorge Ramírez Salas", "Forense de cómputo", "jramirez@laforse-hg.com", "987111222"),
                new Perito("María Torres Quispe", "Forense móvil", "mtorres@laforse-hg.com", "987333444"),
                new Perito("Andrés Quispe Huamán", "Respuesta a incidentes", "aquispe@laforse-hg.com", null)
            ));
        }
        if (casos.count() == 0) {
            List<Servicio> s = servicios.findAllByOrderByIdAsc();
            List<Perito> p = peritos.findAllByOrderByNombresAsc(); // Andrés, Jorge, María
            Perito andres = p.get(0), jorge = p.get(1), maria = p.get(2);
            casos.saveAll(List.of(
                caso("PJ-2026-0142", "Análisis de dispositivo móvil", LocalDate.of(2026, 8, 3), jorge, s.get(4), EstadoCaso.DICTAMEN_ENTREGADO),
                caso("PJ-2026-0158", "Extracción de evidencia en servidor", LocalDate.of(2026, 8, 14), maria, s.get(0), EstadoCaso.EN_ANALISIS),
                caso("PJ-2026-0161", "Auditoría interna - fraude corporativo", LocalDate.of(2026, 8, 20), jorge, s.get(5), EstadoCaso.EN_ANALISIS),
                caso("PJ-2026-0173", "Respuesta a incidente - ransomware", LocalDate.of(2026, 8, 25), andres, s.get(3), EstadoCaso.URGENTE),
                caso("PJ-2026-0180", "Análisis de mensajería (WhatsApp Web)", LocalDate.of(2026, 8, 28), maria, s.get(4), EstadoCaso.PROGRAMADO),
                caso("PJ-2026-0186", "Recuperación de correos eliminados", LocalDate.of(2026, 9, 4), andres, s.get(1), EstadoCaso.DICTAMEN_ENTREGADO),
                caso("PJ-2026-0191", "Peritaje de video de cámara de seguridad", LocalDate.of(2026, 9, 12), jorge, s.get(1), EstadoCaso.EN_ANALISIS),
                caso("PJ-2026-0197", "Fuga de información en área comercial", LocalDate.of(2026, 9, 21), maria, s.get(5), EstadoCaso.PROGRAMADO)
            ));
        }
    }

    private static Caso caso(String exp, String tipo, LocalDate fecha, Perito perito, Servicio servicio, EstadoCaso estado) {
        Caso c = new Caso();
        c.setExpediente(exp);
        c.setTipo(tipo);
        c.setFechaInicio(fecha);
        c.setPerito(perito);
        c.setServicio(servicio);
        c.setEstado(estado);
        return c;
    }
}
