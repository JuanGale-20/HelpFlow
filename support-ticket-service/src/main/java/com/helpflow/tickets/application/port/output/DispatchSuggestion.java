package com.helpflow.tickets.application.port.output;

import com.helpflow.tickets.domain.model.Availability;

/** Respuesta del servicio de despacho, ya traducida al lenguaje del dominio. */
public record DispatchSuggestion(
        String technicianCode,
        Availability availability,
        int estimatedHours
) {
}