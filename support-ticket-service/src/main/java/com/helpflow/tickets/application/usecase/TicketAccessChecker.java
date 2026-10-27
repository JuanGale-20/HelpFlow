package com.helpflow.tickets.application.usecase;

import com.helpflow.tickets.application.exception.TicketAccessDeniedException;
import com.helpflow.tickets.application.port.input.Actor;
import com.helpflow.tickets.application.port.output.TechnicianRepositoryPort;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketStatus;
import org.springframework.stereotype.Component;

/** Reglas de propiedad: quién puede ver o gestionar un ticket según su rol. */
@Component
public class TicketAccessChecker {

    private final TechnicianRepositoryPort technicianRepository;

    public TicketAccessChecker(TechnicianRepositoryPort technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    public void ensureCanView(Ticket ticket, Actor actor) {
        boolean allowed = switch (actor.role()) {
            case ROLE_ADMIN -> true;
            case ROLE_USUARIO -> ticket.isRequestedBy(actor.userId());
            case ROLE_TECNICO -> isAssignedTechnician(ticket, actor);
        };
        if (!allowed) {
            throw new TicketAccessDeniedException("No tienes acceso al ticket " + ticket.getId().value());
        }
    }

    public void ensureCanChangeStatus(Ticket ticket, TicketStatus newStatus, Actor actor) {
        boolean allowed = switch (actor.role()) {
            case ROLE_ADMIN -> true;
            case ROLE_TECNICO -> isAssignedTechnician(ticket, actor);
            case ROLE_USUARIO -> ticket.isRequestedBy(actor.userId()) && newStatus == TicketStatus.CANCELADO;
        };
        if (!allowed) {
            throw new TicketAccessDeniedException(
                    "No puedes cambiar el ticket " + ticket.getId().value() + " a " + newStatus);
        }
    }

    private boolean isAssignedTechnician(Ticket ticket, Actor actor) {
        return technicianRepository.findByUserId(actor.userId())
                .map(technician -> ticket.isAssignedTo(technician.id()))
                .orElse(false);
    }
}
