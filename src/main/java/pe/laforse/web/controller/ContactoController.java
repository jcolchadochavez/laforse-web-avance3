package pe.laforse.web.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.laforse.web.exception.ReglaNegocioException;
import pe.laforse.web.model.ContactoForm;
import pe.laforse.web.model.Solicitud;
import pe.laforse.web.model.Urgencia;
import pe.laforse.web.service.ServicioService;
import pe.laforse.web.service.SolicitudService;

/** Formulario público de solicitud de peritaje: muestra (GET), valida y guarda en MySQL (POST). */
@Controller
@RequestMapping("/contacto")
public class ContactoController {

    private final ServicioService servicioService;
    private final SolicitudService solicitudService;

    public ContactoController(ServicioService servicioService, SolicitudService solicitudService) {
        this.servicioService = servicioService;
        this.solicitudService = solicitudService;
    }

    @ModelAttribute
    public void datosFormulario(Model model) {
        model.addAttribute("servicios", servicioService.listar());
        model.addAttribute("urgencias", Urgencia.values());
    }

    @GetMapping
    public String formulario(@RequestParam(required = false) Integer servicio, Model model) {
        if (!model.containsAttribute("contacto")) {
            ContactoForm form = new ContactoForm();
            form.setServicioId(servicio);
            model.addAttribute("contacto", form);
        }
        return "contacto";
    }

    @PostMapping
    public String enviar(@Valid @ModelAttribute("contacto") ContactoForm contacto,
                         BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "contacto";
        }
        try {
            Solicitud solicitud = solicitudService.registrar(contacto);
            flash.addFlashAttribute("solicitud", solicitud);
            return "redirect:/contacto";
        } catch (ReglaNegocioException ex) {
            result.rejectValue(ex.getCampo(), "regla", ex.getMessage());
            return "contacto";
        }
    }
}
