package pe.laforse.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.laforse.web.service.*;

/** Páginas informativas del sitio (todas por GET). */
@Controller
public class WebController {

    private final ServicioService servicioService;
    private final HerramientaService herramientaService;
    private final CasoService casoService;
    private final EmpresaService empresaService;

    public WebController(ServicioService servicioService, HerramientaService herramientaService,
                         CasoService casoService, EmpresaService empresaService) {
        this.servicioService = servicioService;
        this.herramientaService = herramientaService;
        this.casoService = casoService;
        this.empresaService = empresaService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        var servicios = servicioService.listar();
        model.addAttribute("servicios", servicios.subList(0, Math.min(3, servicios.size())));
        model.addAttribute("totalServicios", servicios.size());
        model.addAttribute("totalCasos", casoService.total());
        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        model.addAttribute("empresa", empresaService.obtener());
        return "nosotros";
    }

    @GetMapping("/servicios")
    public String servicios(Model model) {
        model.addAttribute("servicios", servicioService.listar());
        return "servicios";
    }

    @GetMapping("/servicios/{id}")
    public String detalleServicio(@PathVariable Integer id, Model model) {
        model.addAttribute("servicio", servicioService.obtener(id)); // 404 si no existe
        model.addAttribute("servicios", servicioService.listar());
        return "servicio-detalle";
    }

    @GetMapping("/herramientas")
    public String herramientas(Model model) {
        model.addAttribute("categorias", herramientaService.agruparPorCategoria());
        return "herramientas";
    }
}
