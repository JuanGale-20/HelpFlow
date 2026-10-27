package com.helpflow.tickets.domain.service;

import com.helpflow.tickets.domain.exception.AssignmentNotAllowedException;
import com.helpflow.tickets.domain.model.Technician;
import com.helpflow.tickets.domain.model.Ticket;

public class AssignmentPolicy {

    public boolean isEligible(Ticket ticket, Technician technician, long activeTickets) {
        return technician.isActive()
                && technician.handles(ticket.getCategory())
                && technician.hasCapacity(activeTickets);
    }

    public void validate(Ticket ticket, Technician technician, long activeTickets) {
        if (!technician.isActive()) {
            throw new AssignmentNotAllowedException("El técnico " + technician.code() + " está INACTIVO");
        }
        if (!technician.handles(ticket.getCategory())) {
            throw new AssignmentNotAllowedException("El técnico " + technician.code()
                    + " no atiende la categoría " + ticket.getCategory());
        }
        if (!technician.hasCapacity(activeTickets)) {
            throw new AssignmentNotAllowedException("El técnico " + technician.code()
                    + " alcanzó su carga máxima de tickets activos");
        }
    }
}