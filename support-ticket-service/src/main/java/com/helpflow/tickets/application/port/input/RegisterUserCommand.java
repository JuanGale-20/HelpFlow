package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.model.TicketCategory;

public record RegisterUserCommand(
        String document,
        String name,
        String email,
        String rawPassword,
        String site,
        String area,
        Role role,
        // Campos opcionales solo requeridos si role == ROLE_TECNICO
        String technicianCode,
        TicketCategory specialty,
        Integer maxActiveTickets
) {
}
