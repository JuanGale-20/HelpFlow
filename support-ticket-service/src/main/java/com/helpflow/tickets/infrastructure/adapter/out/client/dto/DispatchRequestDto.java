package com.helpflow.tickets.infrastructure.adapter.out.client.dto;

public record DispatchRequestDto(
        Long ticketId,
        String categoria,
        String prioridad
) {
}
