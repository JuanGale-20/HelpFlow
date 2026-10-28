package com.helpflow.tickets.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class TicketFlowIntegrationTest extends AbstractIntegrationTest {

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    // ---------- Autenticación ----------

    @Test
    void registroPublicoSiempreCreaUsuarioNormal_aunqueSePidaAdmin() throws Exception {
        String body = """
                {"document":"DOC-HACK-1","name":"Intruso","email":"hack1@helpflow.com",
                 "password":"secreto1","site":"Sede Norte","area":"TI","role":"ROLE_ADMIN"}""";
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ROLE_USUARIO"));
    }

    @Test
    void loginConClaveIncorrectaDevuelve401ConProblemDetail() throws Exception {
        String body = """
                {"email":"admin@helpflow.com","password":"incorrecta"}""";
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void consultarSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("No autorizado"));
    }

    // ---------- Creación y propiedad ----------

    @Test
    void usuarioCreaTicketYQuedaAsignado() throws Exception {
        String token = registerAndLogin();
        String body = """
                {"title":"Sin internet","description":"No hay red","category":"RED","priority":"ALTA"}""";
        mockMvc.perform(post("/tickets").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ASIGNADO"))
                .andExpect(jsonPath("$.assignedTechnicianId").isNumber())
                .andExpect(jsonPath("$.slaDeadline").exists());
    }

    @Test
    void ticketConDatosInvalidosDevuelve400ConInvalidParams() throws Exception {
        String token = registerAndLogin();
        mockMvc.perform(post("/tickets").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalidParams").isArray());
    }

    @Test
    void usuarioNoPuedeVerTicketDeOtroUsuario() throws Exception {
        long ticketId = createTicket(registerAndLogin());
        String otro = registerAndLogin();

        mockMvc.perform(get("/tickets/" + ticketId).header("Authorization", bearer(otro)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Acceso denegado"));
    }

    @Test
    void usuarioSoloListaSusPropiosTickets() throws Exception {
        String token = registerAndLogin();
        createTicket(token);
        createTicket(token);

        mockMvc.perform(get("/tickets").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ---------- Roles ----------

    @Test
    void usuarioNoPuedeEntrarAConfiguracionSlaPeroAdminSi() throws Exception {
        mockMvc.perform(get("/admin/sla").header("Authorization", bearer(registerAndLogin())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(get("/admin/sla").header("Authorization", bearer(login("admin@helpflow.com", "admin123"))))
                .andExpect(status().isOk());
    }

    @Test
    void tecnicoNoAsignadoNoPuedeGestionarElTicket() throws Exception {
        long ticketId = createTicket(registerAndLogin());   // categoría RED
        String tecnicoHardware = login("tec01@helpflow.com", "admin123"); // atiende HARDWARE

        mockMvc.perform(patch("/tickets/" + ticketId + "/status")
                        .header("Authorization", bearer(tecnicoHardware))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"EN_PROGRESO"}"""))
                .andExpect(status().isForbidden());
    }

    // ---------- Transiciones ----------

    @Test
    void flujoCompletoDeTransicionesConHistorial() throws Exception {
        long ticketId = createTicket(registerAndLogin());
        String admin = login("admin@helpflow.com", "admin123");

        // ASIGNADO -> EN_PROGRESO (estampa firstResponseAt)
        mockMvc.perform(patch("/tickets/" + ticketId + "/status").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"status":"EN_PROGRESO"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EN_PROGRESO"))
                .andExpect(jsonPath("$.firstResponseAt").exists());

        // RESUELTO sin observación -> 400 (validación cruzada)
        mockMvc.perform(patch("/tickets/" + ticketId + "/status").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"status":"RESUELTO"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalidParams[0].field").value("observation"));

        // RESUELTO con observación -> 200
        mockMvc.perform(patch("/tickets/" + ticketId + "/status").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"status":"RESUELTO","observation":"Se reinició el switch"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolvedAt").exists());

        // Historial: creación + asignación + 2 cambios de estado
        mockMvc.perform(get("/tickets/" + ticketId + "/history").header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(4)));
    }

    @Test
    void transicionInvalidaDevuelve422() throws Exception {
        long ticketId = createTicket(registerAndLogin());
        String admin = login("admin@helpflow.com", "admin123");

        // ASIGNADO -> CERRADO no está permitido
        mockMvc.perform(patch("/tickets/" + ticketId + "/status").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"status":"CERRADO"}"""))
                .andExpect(status().isUnprocessableEntity());
    }
}
