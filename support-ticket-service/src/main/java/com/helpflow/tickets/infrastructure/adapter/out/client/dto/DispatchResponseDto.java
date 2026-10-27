package com.helpflow.tickets.infrastructure.adapter.out.client.dto;

public record DispatchResponseDto(
        Long ticketId,
        String tecnicoId,
        String disponibilidad,
        int tiempoEstimadoHoras
) {
}
