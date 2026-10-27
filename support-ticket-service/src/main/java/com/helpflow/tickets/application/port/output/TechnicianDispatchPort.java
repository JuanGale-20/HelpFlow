package com.helpflow.tickets.application.port.output;

import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.valueobject.TicketId;
import java.util.Optional;

public interface TechnicianDispatchPort {

    /** Vacío si el servicio externo no responde o no sugiere a nadie. Nunca lanza excepción. */
    Optional<DispatchSuggestion> suggest(TicketId ticketId, TicketCategory category, TicketPriority priority);
}