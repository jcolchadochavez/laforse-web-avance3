package pe.laforse.web.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.laforse.web.dto.CasoForm;
import pe.laforse.web.exception.ReglaNegocioException;
import pe.laforse.web.model.Caso;
import pe.laforse.web.model.EstadoCaso;
import pe.laforse.web.service.CasoService;
import pe.laforse.web.service.PeritoService;
import pe.laforse.web.service.ServicioService;

/** CRUD de casos periciales: listar, registrar, editar y eliminar. */
@Controller
@RequestMapping("/casos")
public class CasoController {

    private final CasoService casoService;
    private final PeritoService peritoService;
    private final ServicioService servicioService;

    public CasoController(CasoService casoService, PeritoService peritoService, ServicioService servicioService) {
        this.casoService = casoService;
        this.peritoService = peritoService;
        this.servicioService = servicioService;
    }

    // ---------- READ ----------
    @GetMapping
    public String listar(@RequestParam(required = false) EstadoCaso estado,
                         @RequestParam(required = false) String q, Model model) {
        model.addAttribute("casos", casoService.listar(estado, q));
        model.addAttribute("conteo", casoService.contarPorEstado());
        model.addAttribute("estados", EstadoCaso.values());
        model.addAttribute("estadoSel", estado);
        model.addAttribute("q", q);
        model.addAttribute("totalCasos", casoService.total());
        return "casos";
    }

    // ---------- CREATE ----------
    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Long solicitud, Model model) {
        model.addAttribute("caso", casoService.nuevoFormulario(solicitud));
        return formulario(model, null);
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("caso") CasoForm caso, BindingResult result,
                            Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            return formulario(model, null);
        }
        try {
            Caso nuevo = casoService.registrar(caso);
            flash.addFlashAttribute("exito", "Caso " + nuevo.getExpediente() + " registrado correctamente.");
            return "redirect:/casos";
        } catch (ReglaNegocioException ex) {
            rechazar(result, ex);
            return formulario(model, null);
        }
    }

    // ---------- UPDATE ----------
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Caso c = casoService.obtener(id);
        if (c.isCerrado()) {
            flash.addFlashAttribute("error", "El caso " + c.getExpediente() + " tiene el dictamen entregado y ya no se puede modificar.");
            return "redirect:/casos";
        }
        model.addAttribute("caso", casoService.aFormulario(c));
        return formulario(model, id);
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("caso") CasoForm caso,
                             BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            return formulario(model, id);
        }
        try {
            Caso c = casoService.actualizar(id, caso);
            flash.addFlashAttribute("exito", "Caso " + c.getExpediente() + " actualizado correctamente.");
            return "redirect:/casos";
        } catch (ReglaNegocioException ex) {
            rechazar(result, ex);
            return formulario(model, id);
        }
    }

    // ---------- DELETE ----------
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            String exp = casoService.obtener(id).getExpediente();
            casoService.eliminar(id);
            flash.addFlashAttribute("exito", "Caso " + exp + " eliminado.");
        } catch (ReglaNegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/casos";
    }

    private String formulario(Model model, Long id) {
        model.addAttribute("casoId", id);
        model.addAttribute("estados", EstadoCaso.values());
        model.addAttribute("peritos", id == null ? peritoService.listarActivos() : peritoService.listar());
        model.addAttribute("servicios", servicioService.listar());
        return "caso-form";
    }

    private static void rechazar(BindingResult result, ReglaNegocioException ex) {
        if (ex.getCampo() != null) result.rejectValue(ex.getCampo(), "regla", ex.getMessage());
        else result.reject("regla", ex.getMessage());
    }
}
