package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.valueobject.TicketId;
import java.util.Optional;

public interface AssignTicketUseCase {

    /** Asignación automática con la sugerencia del servicio de despacho. Vacío si no se pudo asignar. */
    Optional<Ticket> autoAssign(TicketId ticketId);

    /** Asignación o reasignación manual (admin). Valida las reglas y lanza excepción si no se cumplen. */
    Ticket assignManually(AssignTicketCommand command);
}