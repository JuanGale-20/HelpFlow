package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.model.TicketStatus;
import java.time.Instant;

public record TicketResponse(
        Long id,
        Long requesterId,
        String title,
        String description,
        TicketCategory category,
        TicketPriority priority,
        String site,
        String affectedAsset,
        Instant createdAt,
        Instant slaDeadline,
        TicketStatus status,
        Long assignedTechnicianId,
        Instant firstResponseAt,
        Instant resolvedAt,
        int escalationCount
) {
    public static TicketResponse fromDomain(Ticket ticket) {
        return new TicketResponse(
                ticket.getId() != null ? ticket.getId().value() : null,
                ticket.getRequesterId() != null ? ticket.getRequesterId().value() : null,
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getSite(),
                ticket.getAffectedAsset(),
                ticket.getCreatedAt(),
                ticket.getSlaDeadline() != null ? ticket.getSlaDeadline().value() : null,
                ticket.getStatus(),
                ticket.getAssignedTechnicianId() != null ? ticket.getAssignedTechnicianId().value() : null,
                ticket.getFirstResponseAt(),
                ticket.getResolvedAt(),
                ticket.getEscalationCount()
        );
    }
}
