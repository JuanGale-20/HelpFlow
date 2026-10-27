package com.helpflow.tickets.application.port.output;

import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.valueobject.TicketId;
import java.util.List;

public interface HistoryRepositoryPort {

    AssignmentHistory save(AssignmentHistory history);

    List<AssignmentHistory> findByTicketId(TicketId ticketId);
}