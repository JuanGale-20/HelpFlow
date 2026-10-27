package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest(
        @NotBlank(message = "El título es obligatorio")
        String title,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotNull(message = "La categoría es obligatoria")
        TicketCategory category,

        @NotNull(message = "La prioridad es obligatoria")
        TicketPriority priority,

        String site,
        String affectedAsset
) {
}
