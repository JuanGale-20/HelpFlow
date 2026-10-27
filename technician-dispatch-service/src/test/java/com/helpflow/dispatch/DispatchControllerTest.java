package com.helpflow.dispatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DispatchControllerTest {

    private static final String BODY = """
            {"ticketId": 125, "categoria": "RED", "prioridad": "ALTA"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void mismaCombinacionDevuelveMismoResultado() throws Exception {
        String first = mockMvc.perform(post("/technician-dispatch")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String second = mockMvc.perform(post("/technician-dispatch")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andReturn().getResponse().getContentAsString();

        assertEquals(first, second);
    }

    @Test
    void respuestaTieneLaEstructuraEsperada() throws Exception {
        mockMvc.perform(post("/technician-dispatch")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(125))
                .andExpect(jsonPath("$.tecnicoId").value(org.hamcrest.Matchers.matchesPattern("TEC-\\d{3}")))
                .andExpect(jsonPath("$.disponibilidad").isNotEmpty())
                .andExpect(jsonPath("$.tiempoEstimadoHoras").isNumber());
    }

    @Test
    void cuerpoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post("/technician-dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ticketId\": 1, \"categoria\": \"INEXISTENTE\", \"prioridad\": \"ALTA\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/technician-dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoria\": \"RED\", \"prioridad\": \"ALTA\"}"))
                .andExpect(status().isBadRequest());
    }
}