package com.helpflow.tickets.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.helpflow.tickets.TestcontainersConfiguration;
import com.helpflow.tickets.application.port.output.TechnicianDispatchPort;
import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected TechnicianDispatchPort dispatchPort;

    protected String login(String email, String password) throws Exception {
        String body = """
                {"email":"%s","password":"%s"}""".formatted(email, password);
        String json = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.token");
    }

    /** Registra un usuario único y devuelve su token. */
    protected String registerAndLogin() throws Exception {
        String u = UUID.randomUUID().toString().substring(0, 8);
        String body = """
                {"document":"DOC-%s","name":"Usuario %s","email":"%s@helpflow.com",
                 "password":"secreto1","site":"Sede Central","area":"Ventas"}""".formatted(u, u, u);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        return login(u + "@helpflow.com", "secreto1");
    }

    protected long createTicket(String token) throws Exception {
        String body = """
                {"title":"Sin internet","description":"No hay red en el piso 3",
                 "category":"RED","priority":"ALTA","affectedAsset":"Switch-03"}""";
        String json = mockMvc.perform(post("/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }
}
