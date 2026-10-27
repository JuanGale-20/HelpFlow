package com.helpflow.tickets.domain.model;

import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import java.time.Instant;

/**
 * Registro de una asignación. Si la asignación no vino del servicio de despacho
 * (por ejemplo, una reasignación del admin), estimatedHours y availability pueden ser null.
 */
public record Assignment(
        Long id,
        TicketId ticketId,
        TechnicianId technicianId,
        Instant assignedAt,
        Integer estimatedHours,
        Availability availability
) {

    public Assignment {
        Preconditions.requireNonNull(ticketId, "El ticket de la asignación es obligatorio");
        Preconditions.requireNonNull(technicianId, "El técnico de la asignación es obligatorio");
        Preconditions.requireNonNull(assignedAt, "La fecha de asignación es obligatoria");
    }
}