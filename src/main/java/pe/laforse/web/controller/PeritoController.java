package pe.laforse.web.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.laforse.web.exception.ReglaNegocioException;
import pe.laforse.web.model.Perito;
import pe.laforse.web.service.PeritoService;

/** CRUD de peritos. */
@Controller
@RequestMapping("/peritos")
public class PeritoController {

    private final PeritoService peritoService;

    public PeritoController(PeritoService peritoService) {
        this.peritoService = peritoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("peritos", peritoService.listar());
        model.addAttribute("casosPorPerito", peritoService.casosPorPerito());
        return "peritos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("perito", new Perito());
        return "perito-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("perito", peritoService.obtener(id));
        return "perito-form";
    }

    /** Sirve para registrar (id vacío) y para actualizar (id con valor). */
    @PostMapping
    public String guardar(@Valid @ModelAttribute("perito") Perito perito, BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "perito-form";
        }
        boolean nuevo = perito.getId() == null;
        try {
            Perito p = peritoService.guardar(perito);
            flash.addFlashAttribute("exito", "Perito " + p.getNombres() + (nuevo ? " registrado." : " actualizado."));
            return "redirect:/peritos";
        } catch (ReglaNegocioException ex) {
            result.rejectValue(ex.getCampo(), "regla", ex.getMessage());
            return "perito-form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            String nombre = peritoService.obtener(id).getNombres();
            peritoService.eliminar(id);
            flash.addFlashAttribute("exito", "Perito " + nombre + " eliminado.");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/peritos";
    }
}
