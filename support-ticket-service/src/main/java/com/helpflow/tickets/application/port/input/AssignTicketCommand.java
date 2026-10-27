package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;

public record AssignTicketCommand(
        TicketId ticketId,
        TechnicianId technicianId,
        UserId actorId,
        String observation
) {
}