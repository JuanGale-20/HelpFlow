package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.model.TicketStatus;
import java.time.Instant;

public record AssignmentHistoryResponse(
        Long id,
        Long ticketId,
        TicketStatus previousStatus,
        TicketStatus newStatus,
        Long actorId,
        String observation,
        Instant occurredAt
) {
    public static AssignmentHistoryResponse fromDomain(AssignmentHistory history) {
        return new AssignmentHistoryResponse(
                history.id(),
                history.ticketId() != null ? history.ticketId().value() : null,
                history.previousStatus(),
                history.newStatus(),
                history.actorId() != null ? history.actorId().value() : null,
                history.observation(),
                history.occurredAt()
        );
    }
}
