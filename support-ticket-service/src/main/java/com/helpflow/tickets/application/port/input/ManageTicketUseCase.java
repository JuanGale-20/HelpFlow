package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.Ticket;

public interface ManageTicketUseCase {

    Ticket changeStatus(ChangeStatusCommand command);
}
