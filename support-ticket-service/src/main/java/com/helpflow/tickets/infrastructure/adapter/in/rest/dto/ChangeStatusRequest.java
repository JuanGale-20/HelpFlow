package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import com.helpflow.tickets.domain.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        TicketStatus status,

        String observation
) {
}
