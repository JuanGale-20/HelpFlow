package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.valueobject.TicketId;
import java.util.List;

public interface QueryTicketsUseCase {

    Ticket getById(TicketId ticketId, Actor actor);

    List<Ticket> list(Actor actor);

    List<AssignmentHistory> history(TicketId ticketId, Actor actor);
}
