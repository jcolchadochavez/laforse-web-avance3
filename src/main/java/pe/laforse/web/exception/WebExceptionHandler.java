package pe.laforse.web.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Errores de las vistas Thymeleaf: muestran templates/error.html con un mensaje claro. */
@ControllerAdvice(annotations = Controller.class)
public class WebExceptionHandler {

    private final pe.laforse.web.controller.GlobalModelAdvice comunes;

    public WebExceptionHandler(pe.laforse.web.controller.GlobalModelAdvice comunes) {
        this.comunes = comunes;
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(RecursoNoEncontradoException ex, Model model) {
        return vista(model, 404, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String integridad(DataIntegrityViolationException ex, Model model) {
        return vista(model, 409, "La operación no se pudo completar porque viola una restricción de la base de datos.");
    }

    private String vista(Model model, int status, String mensaje) {
        // Los @ModelAttribute de GlobalModelAdvice no se ejecutan en un @ExceptionHandler: se agregan aquí
        model.addAttribute("anio", comunes.anio());
        model.addAttribute("correoEmpresa", comunes.correo());
        model.addAttribute("telefonoEmpresa", comunes.telefono());
        model.addAttribute("ciudadEmpresa", comunes.ciudad());
        model.addAttribute("status", status);
        model.addAttribute("mensaje", mensaje);
        return "error";
    }
}
