package pe.laforse.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Year;

/** Datos comunes a todas las vistas (navbar y footer). */
@ControllerAdvice(annotations = org.springframework.stereotype.Controller.class)
public class GlobalModelAdvice {

    @Value("${laforse.contacto.correo}")
    private String correo;

    @Value("${laforse.contacto.telefono}")
    private String telefono;

    @Value("${laforse.contacto.ciudad}")
    private String ciudad;

    @ModelAttribute("anio")
    public int anio() {
        return Year.now().getValue();
    }

    @ModelAttribute("correoEmpresa")
    public String correo() { return correo; }

    @ModelAttribute("telefonoEmpresa")
    public String telefono() { return telefono; }

    @ModelAttribute("ciudadEmpresa")
    public String ciudad() { return ciudad; }

    /** Ruta actual, usada por la navbar para marcar el enlace activo. */
    @ModelAttribute("rutaActual")
    public String rutaActual(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
