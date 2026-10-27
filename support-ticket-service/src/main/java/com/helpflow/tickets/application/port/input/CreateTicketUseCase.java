package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.Ticket;

public interface CreateTicketUseCase {

    Ticket create(CreateTicketCommand command);
}