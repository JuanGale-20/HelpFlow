package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record AssignTicketRequest(
        @NotNull(message = "El id del técnico es obligatorio")
        Long technicianId,

        String observation
) {
}
