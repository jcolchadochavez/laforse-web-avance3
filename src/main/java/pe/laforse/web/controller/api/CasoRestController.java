package pe.laforse.web.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.laforse.web.dto.CasoForm;
import pe.laforse.web.dto.CasoResponse;
import pe.laforse.web.model.Caso;
import pe.laforse.web.model.EstadoCaso;
import pe.laforse.web.service.CasoService;

import java.net.URI;
import java.util.List;

/** API REST de casos (JSON). Usa el mismo CasoService que las vistas Thymeleaf. */
@RestController
@RequestMapping("/api/casos")
public class CasoRestController {

    private final CasoService casoService;

    public CasoRestController(CasoService casoService) {
        this.casoService = casoService;
    }

    @GetMapping
    public List<CasoResponse> listar(@RequestParam(required = false) EstadoCaso estado,
                                     @RequestParam(required = false) String q) {
        return casoService.listar(estado, q).stream().map(CasoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public CasoResponse obtener(@PathVariable Long id) {
        return CasoResponse.de(casoService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<CasoResponse> registrar(@Valid @RequestBody CasoForm form) {
        Caso c = casoService.registrar(form);
        return ResponseEntity.created(URI.create("/api/casos/" + c.getId())).body(CasoResponse.de(c));
    }

    @PutMapping("/{id}")
    public CasoResponse actualizar(@PathVariable Long id, @Valid @RequestBody CasoForm form) {
        return CasoResponse.de(casoService.actualizar(id, form));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        casoService.eliminar(id);
    }
}
