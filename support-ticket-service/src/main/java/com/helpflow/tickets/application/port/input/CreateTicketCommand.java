package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.valueobject.UserId;

public record CreateTicketCommand(
        UserId requesterId,
        String title,
        String description,
        TicketCategory category,
        TicketPriority priority,
        String site,           // opcional: si viene vacío se usa la sede del usuario
        String affectedAsset
) {
}