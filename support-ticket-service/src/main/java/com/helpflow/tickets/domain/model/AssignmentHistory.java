package com.helpflow.tickets.domain.model;

import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.time.Instant;

/** Historial de cambios de un ticket: quién, cuándo, de qué estado a cuál y con qué observación. */
public record AssignmentHistory(
        Long id,
        TicketId ticketId,
        TicketStatus previousStatus,
        TicketStatus newStatus,
        UserId actorId,
        String observation,
        Instant occurredAt
) {

    public AssignmentHistory {
        Preconditions.requireNonNull(ticketId, "El ticket del historial es obligatorio");
        Preconditions.requireNonNull(newStatus, "El nuevo estado es obligatorio");
        Preconditions.requireNonNull(occurredAt, "La fecha del evento es obligatoria");
    }

    public static AssignmentHistory of(TicketId ticketId, TicketStatus previousStatus, TicketStatus newStatus,
                                       UserId actorId, String observation, Instant occurredAt) {
        return new AssignmentHistory(null, ticketId, previousStatus, newStatus, actorId, observation, occurredAt);
    }
}