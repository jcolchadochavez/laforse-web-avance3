package pe.laforse.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import pe.laforse.web.repository.SolicitudRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Pruebas de las páginas públicas y del formulario de contacto (ahora guarda en BD). */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class WebControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SolicitudRepository solicitudes;

    @Test
    void paginasPrincipalesRespondenOk() throws Exception {
        for (String ruta : new String[]{"/", "/nosotros", "/servicios", "/servicios/1", "/herramientas",
                "/casos", "/casos/nuevo", "/peritos", "/peritos/nuevo", "/solicitudes", "/contacto"}) {
            mvc.perform(get(ruta)).andExpect(status().isOk());
        }
    }

    @Test
    void filtraCasosPorEstado() throws Exception {
        mvc.perform(get("/casos").param("estado", "URGENTE"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("casos", hasSize(1)));
    }

    @Test
    void servicioInexistenteDevuelve404() throws Exception {
        mvc.perform(get("/servicios/99")).andExpect(status().isNotFound());
    }

    @Test
    void formularioInvalidoMuestraErrores() throws Exception {
        mvc.perform(post("/contacto").param("nombre", "").param("correo", "no-es-correo"))
            .andExpect(status().isOk())
            .andExpect(view().name("contacto"))
            .andExpect(model().attributeHasFieldErrors("contacto", "nombre", "correo", "servicioId", "urgencia", "mensaje", "acepta"));
    }

    @Test
    void formularioValidoGuardaEnBaseDeDatos() throws Exception {
        long antes = solicitudes.count();
        mvc.perform(post("/contacto")
                .param("nombre", "Ana Torres")
                .param("correo", "ana@empresa.com")
                .param("telefono", "987654321")
                .param("servicioId", "5")
                .param("urgencia", "ALTA")
                .param("mensaje", "Necesito extraer los chats de un celular para un proceso laboral.")
                .param("acepta", "true"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/contacto"))
            .andExpect(flash().attribute("solicitud", org.hamcrest.Matchers.hasProperty("codigo", containsString("SOL-"))));
        assertThat(solicitudes.count()).isEqualTo(antes + 1);
    }
}
