package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.infrastructure.adapter.in.rest.validation.ValidSlaHours;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

@ValidSlaHours
public record ConfigureSlaRequest(
        @NotEmpty(message = "El mapa de horas por prioridad no puede estar vacío")
        Map<TicketPriority, Integer> hours
) {
}
