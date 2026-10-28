package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.infrastructure.adapter.in.rest.validation.ValidStatusChange;
import jakarta.validation.constraints.NotNull;

@ValidStatusChange
public record ChangeStatusRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        TicketStatus status,

        String observation
) {
}
