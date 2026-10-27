package com.helpflow.tickets.application.usecase;

import com.helpflow.tickets.application.exception.ResourceNotFoundException;
import com.helpflow.tickets.application.port.input.Actor;
import com.helpflow.tickets.application.port.input.QueryTicketsUseCase;
import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.application.port.output.TechnicianRepositoryPort;
import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.valueobject.TicketId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QueryTicketsService implements QueryTicketsUseCase {

    private final TicketRepositoryPort ticketRepository;
    private final TechnicianRepositoryPort technicianRepository;
    private final HistoryRepositoryPort historyRepository;
    private final TicketAccessChecker accessChecker;

    public QueryTicketsService(TicketRepositoryPort ticketRepository,
                               TechnicianRepositoryPort technicianRepository,
                               HistoryRepositoryPort historyRepository,
                               TicketAccessChecker accessChecker) {
        this.ticketRepository = ticketRepository;
        this.technicianRepository = technicianRepository;
        this.historyRepository = historyRepository;
        this.accessChecker = accessChecker;
    }

    @Override
    public Ticket getById(TicketId ticketId, Actor actor) {
        Ticket ticket = loadTicket(ticketId);
        accessChecker.ensureCanView(ticket, actor);
        return ticket;
    }

    @Override
    public List<Ticket> list(Actor actor) {
        return switch (actor.role()) {
            case ROLE_ADMIN -> ticketRepository.findAll();
            case ROLE_USUARIO -> ticketRepository.findByRequesterId(actor.userId());
            case ROLE_TECNICO -> technicianRepository.findByUserId(actor.userId())
                    .map(technician -> ticketRepository.findByAssignedTechnicianId(technician.id()))
                    .orElse(List.of());
        };
    }

    @Override
    public List<AssignmentHistory> history(TicketId ticketId, Actor actor) {
        Ticket ticket = loadTicket(ticketId);
        accessChecker.ensureCanView(ticket, actor);
        return historyRepository.findByTicketId(ticketId);
    }

    private Ticket loadTicket(TicketId ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el ticket " + ticketId.value()));
    }
}
