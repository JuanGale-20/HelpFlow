package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.domain.valueobject.TicketId;

public record ChangeStatusCommand(
        TicketId ticketId,
        TicketStatus newStatus,
        Actor actor,
        String observation
) {
}
