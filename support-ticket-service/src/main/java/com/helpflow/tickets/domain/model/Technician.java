package com.helpflow.tickets.domain.model;

import com.helpflow.tickets.domain.exception.DomainException;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.UserId;

public record Technician(
        TechnicianId id,
        String code,
        UserId userId,
        String name,
        TicketCategory specialty,
        String site,
        int maxActiveTickets,
        UserStatus status
) {

    public Technician {
        Preconditions.requireText(code, "El código del técnico es obligatorio");
        Preconditions.requireText(name, "El nombre del técnico es obligatorio");
        Preconditions.requireText(site, "La sede del técnico es obligatoria");
        Preconditions.requireNonNull(specialty, "La especialidad es obligatoria");
        Preconditions.requireNonNull(status, "El estado es obligatorio");
        if (maxActiveTickets < 1) {
            throw new DomainException("La carga máxima debe ser de al menos 1 ticket");
        }
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVO;
    }

    /** Un técnico atiende su especialidad; las solicitudes de categoría OTRO las puede atender cualquiera. */
    public boolean handles(TicketCategory category) {
        return specialty == category || category == TicketCategory.OTRO;
    }

    public boolean hasCapacity(long activeTickets) {
        return activeTickets < maxActiveTickets;
    }
}