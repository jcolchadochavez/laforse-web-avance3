package pe.laforse.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import pe.laforse.web.model.Caso;
import pe.laforse.web.model.EstadoCaso;
import pe.laforse.web.model.Perito;
import pe.laforse.web.repository.CasoRepository;
import pe.laforse.web.repository.PeritoRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** CRUD de casos y peritos contra la base de datos, incluidas las reglas de negocio. */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CasoCrudTest {

    @Autowired private MockMvc mvc;
    @Autowired private CasoRepository casos;
    @Autowired private PeritoRepository peritos;

    private Long idPerito() {
        return peritos.findByActivoTrueOrderByNombresAsc().get(0).getId();
    }

    private Caso casoPorExpediente(String exp) {
        return casos.findAll().stream().filter(c -> c.getExpediente().equals(exp)).findFirst().orElseThrow();
    }

    @Test
    void registraCasoValido() throws Exception {
        mvc.perform(post("/casos")
                .param("expediente", "PJ-2026-0500").param("tipo", "Análisis de laptop corporativa")
                .param("fechaInicio", LocalDate.now().toString()).param("estado", "PROGRAMADO")
                .param("peritoId", idPerito().toString()).param("servicioId", "2"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/casos"))
            .andExpect(flash().attributeExists("exito"));
        assertThat(casos.existsByExpediente("PJ-2026-0500")).isTrue();
    }

    @Test
    void sugiereSiguienteExpediente() throws Exception {
        int anio = java.time.Year.now().getValue();
        String esperado = anio == 2026 ? "PJ-2026-0198" : "PJ-" + anio + "-0001";
        mvc.perform(get("/casos/nuevo"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("caso", org.hamcrest.Matchers.hasProperty("expediente", org.hamcrest.Matchers.is(esperado))));
    }

    @Test
    void rechazaDatosInvalidos() throws Exception {
        mvc.perform(post("/casos")
                .param("expediente", "123").param("tipo", "abc")
                .param("fechaInicio", LocalDate.now().plusDays(5).toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("caso-form"))
            .andExpect(model().attributeHasFieldErrors("caso", "expediente", "tipo", "fechaInicio", "estado", "peritoId", "servicioId"));
    }

    @Test
    void rechazaExpedienteDuplicado() throws Exception {
        mvc.perform(post("/casos")
                .param("expediente", "PJ-2026-0142").param("tipo", "Caso duplicado de prueba")
                .param("fechaInicio", "2026-09-01").param("estado", "PROGRAMADO")
                .param("peritoId", idPerito().toString()).param("servicioId", "1"))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrorCode("caso", "expediente", "regla"));
    }

    @Test
    void actualizaCaso() throws Exception {
        Caso c = casoPorExpediente("PJ-2026-0180");
        mvc.perform(post("/casos/" + c.getId())
                .param("expediente", "PJ-2026-0180").param("tipo", "Análisis de mensajería (WhatsApp y Telegram)")
                .param("fechaInicio", "2026-08-28").param("estado", "EN_ANALISIS")
                .param("peritoId", c.getPerito().getId().toString()).param("servicioId", "5"))
            .andExpect(status().is3xxRedirection());
        Caso editado = casos.findById(c.getId()).orElseThrow();
        assertThat(editado.getEstado()).isEqualTo(EstadoCaso.EN_ANALISIS);
        assertThat(editado.getTipo()).contains("Telegram");
    }

    @Test
    void noEditaNiEliminaCasoCerrado() throws Exception {
        Caso c = casoPorExpediente("PJ-2026-0142"); // Dictamen entregado
        mvc.perform(get("/casos/" + c.getId() + "/editar"))
            .andExpect(redirectedUrl("/casos")).andExpect(flash().attributeExists("error"));
        mvc.perform(post("/casos/" + c.getId() + "/eliminar"))
            .andExpect(redirectedUrl("/casos")).andExpect(flash().attributeExists("error"));
        assertThat(casos.existsById(c.getId())).isTrue();
    }

    @Test
    void eliminaCasoAbierto() throws Exception {
        Caso c = casoPorExpediente("PJ-2026-0197");
        mvc.perform(post("/casos/" + c.getId() + "/eliminar"))
            .andExpect(redirectedUrl("/casos")).andExpect(flash().attributeExists("exito"));
        assertThat(casos.existsById(c.getId())).isFalse();
    }

    @Test
    void casoInexistenteDevuelve404() throws Exception {
        mvc.perform(get("/casos/9999/editar")).andExpect(status().isNotFound());
    }

    @Test
    void noEliminaPeritoConCasos() throws Exception {
        Long id = idPerito();
        mvc.perform(post("/peritos/" + id + "/eliminar"))
            .andExpect(redirectedUrl("/peritos")).andExpect(flash().attributeExists("error"));
        assertThat(peritos.existsById(id)).isTrue();
    }

    @Test
    void registraYEliminaPeritoSinCasos() throws Exception {
        mvc.perform(post("/peritos")
                .param("nombres", "Lucía Paredes Rojas").param("especialidad", "Auditoría forense")
                .param("correo", "LParedes@laforse-hg.com").param("telefono", "912345678").param("activo", "true"))
            .andExpect(redirectedUrl("/peritos"));
        Perito p = peritos.findAll().stream().filter(x -> x.getCorreo().equals("lparedes@laforse-hg.com")).findFirst().orElseThrow();
        mvc.perform(post("/peritos/" + p.getId() + "/eliminar")).andExpect(flash().attributeExists("exito"));
        assertThat(peritos.existsById(p.getId())).isFalse();
    }

    @Test
    void rechazaCorreoDePeritoDuplicado() throws Exception {
        mvc.perform(post("/peritos")
                .param("nombres", "Otro Perito").param("especialidad", "Forense móvil")
                .param("correo", "mtorres@laforse-hg.com"))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrorCode("perito", "correo", "regla"));
    }
}
