package com.helpflow.dispatch.dto;

import jakarta.validation.constraints.NotNull;

public record DispatchRequest(
        @NotNull(message = "ticketId es obligatorio") Long ticketId,
        @NotNull(message = "categoria es obligatoria") Categoria categoria,
        @NotNull(message = "prioridad es obligatoria") Prioridad prioridad
) {

    public enum Categoria {HARDWARE, SOFTWARE, RED, ACCESOS, OTRO}

    public enum Prioridad {BAJA, MEDIA, ALTA, CRITICA}
}