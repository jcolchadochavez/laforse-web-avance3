package pe.laforse.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import pe.laforse.web.repository.PeritoRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** API REST /api/casos: respuestas JSON y códigos HTTP. */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CasoApiTest {

    @Autowired private MockMvc mvc;
    @Autowired private PeritoRepository peritos;

    private String json(String expediente, String estado) {
        Long perito = peritos.findByActivoTrueOrderByNombresAsc().get(0).getId();
        return """
            {"expediente":"%s","tipo":"Análisis de USB incautado","fechaInicio":"2026-10-20",
             "estado":"%s","peritoId":%d,"servicioId":2}
            """.formatted(expediente, estado, perito);
    }

    @Test
    void listaCasosEnJson() throws Exception {
        mvc.perform(get("/api/casos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(8)))
            .andExpect(jsonPath("$[0].expediente", startsWith("PJ-")));
    }

    @Test
    void crudCompletoPorApi() throws Exception {
        String location = mvc.perform(post("/api/casos").contentType(MediaType.APPLICATION_JSON).content(json("PJ-2026-0700", "PROGRAMADO")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.expediente").value("PJ-2026-0700"))
            .andReturn().getResponse().getHeader("Location");

        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).content(json("PJ-2026-0700", "EN_ANALISIS")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("EN_ANALISIS"));

        mvc.perform(delete(location)).andExpect(status().isNoContent());
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    void validacionDevuelve400ConCampos() throws Exception {
        mvc.perform(post("/api/casos").contentType(MediaType.APPLICATION_JSON).content("{\"expediente\":\"X\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.expediente").exists())
            .andExpect(jsonPath("$.campos.peritoId").exists());
    }

    @Test
    void reglaDeNegocioDevuelve409() throws Exception {
        mvc.perform(post("/api/casos").contentType(MediaType.APPLICATION_JSON).content(json("PJ-2026-0142", "PROGRAMADO")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.campo").value("expediente"));
    }
}
