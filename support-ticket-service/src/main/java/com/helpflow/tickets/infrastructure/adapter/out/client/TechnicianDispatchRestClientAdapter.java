package com.helpflow.tickets.infrastructure.adapter.out.client;

import com.helpflow.tickets.application.port.output.DispatchSuggestion;
import com.helpflow.tickets.application.port.output.TechnicianDispatchPort;
import com.helpflow.tickets.domain.model.Availability;
import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.infrastructure.adapter.out.client.dto.DispatchRequestDto;
import com.helpflow.tickets.infrastructure.adapter.out.client.dto.DispatchResponseDto;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TechnicianDispatchRestClientAdapter implements TechnicianDispatchPort {

    private static final Logger log = LoggerFactory.getLogger(TechnicianDispatchRestClientAdapter.class);

    private final RestClient restClient;

    public TechnicianDispatchRestClientAdapter(@Value("${dispatch.service.url:http://localhost:8081}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public Optional<DispatchSuggestion> suggest(TicketId ticketId, TicketCategory category, TicketPriority priority) {
        if (ticketId == null || category == null || priority == null) {
            return Optional.empty();
        }

        DispatchRequestDto request = new DispatchRequestDto(
                ticketId.value(),
                category.name(),
                priority.name()
        );

        try {
            DispatchResponseDto response = restClient.post()
                    .uri("/technician-dispatch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(DispatchResponseDto.class);

            if (response == null || response.tecnicoId() == null || response.tecnicoId().isBlank()) {
                log.warn("El servicio de despacho devolvió una respuesta vacía para el ticket {}", ticketId.value());
                return Optional.empty();
            }

            Availability availability;
            try {
                availability = Availability.valueOf(response.disponibilidad().toUpperCase());
            } catch (Exception e) {
                availability = Availability.MEDIA;
            }

            return Optional.of(new DispatchSuggestion(
                    response.tecnicoId(),
                    availability,
                    response.tiempoEstimadoHoras()
            ));
        } catch (Exception ex) {
            log.warn("Fallo al comunicarse con technician-dispatch-service para el ticket {}: {}",
                    ticketId.value(), ex.getMessage());
            return Optional.empty();
        }
    }
}
