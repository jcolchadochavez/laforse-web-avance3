package pe.laforse.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.laforse.web.exception.ReglaNegocioException;
import pe.laforse.web.model.EstadoSolicitud;
import pe.laforse.web.service.SolicitudService;

/** Bandeja interna de solicitudes recibidas desde el formulario de contacto. */
@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoSolicitud estado, Model model) {
        model.addAttribute("solicitudes", solicitudService.listar(estado));
        model.addAttribute("conteo", solicitudService.contarPorEstado());
        model.addAttribute("estados", EstadoSolicitud.values());
        model.addAttribute("estadoSel", estado);
        return "solicitudes";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam EstadoSolicitud estado, RedirectAttributes flash) {
        solicitudService.cambiarEstado(id, estado);
        flash.addFlashAttribute("exito", "Solicitud " + solicitudService.obtener(id).getCodigo()
            + " marcada como " + estado.getEtiqueta().toLowerCase() + ".");
        return "redirect:/solicitudes";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            String codigo = solicitudService.obtener(id).getCodigo();
            solicitudService.eliminar(id);
            flash.addFlashAttribute("exito", "Solicitud " + codigo + " eliminada.");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/solicitudes";
    }
}
