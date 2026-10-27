package com.helpflow.tickets.application.usecase;

import com.helpflow.tickets.application.exception.ResourceNotFoundException;
import com.helpflow.tickets.application.port.input.AssignTicketUseCase;
import com.helpflow.tickets.application.port.input.CreateTicketCommand;
import com.helpflow.tickets.application.port.input.CreateTicketUseCase;
import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.application.port.output.SlaConfigurationPort;
import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.application.port.output.UserRepositoryPort;
import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.domain.model.User;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTicketService implements CreateTicketUseCase {

    private final UserRepositoryPort userRepository;
    private final TicketRepositoryPort ticketRepository;
    private final HistoryRepositoryPort historyRepository;
    private final SlaConfigurationPort slaConfiguration;
    private final AssignTicketUseCase assignTicket;
    private final Clock clock;

    public CreateTicketService(UserRepositoryPort userRepository,
                               TicketRepositoryPort ticketRepository,
                               HistoryRepositoryPort historyRepository,
                               SlaConfigurationPort slaConfiguration,
                               AssignTicketUseCase assignTicket,
                               Clock clock) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.slaConfiguration = slaConfiguration;
        this.assignTicket = assignTicket;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Ticket create(CreateTicketCommand command) {
        User requester = userRepository.findById(command.requesterId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el usuario " + command.requesterId().value()));
        requester.ensureCanCreateTickets();

        Instant now = clock.instant();
        String site = (command.site() == null || command.site().isBlank())
                ? requester.site()
                : command.site();

        Ticket ticket = Ticket.create(
                command.requesterId(),
                command.title(),
                command.description(),
                command.category(),
                command.priority(),
                site,
                command.affectedAsset(),
                slaConfiguration.currentPolicy().calculateDeadline(command.priority(), now),
                now);

        Ticket saved = ticketRepository.save(ticket);
        historyRepository.save(AssignmentHistory.of(
                saved.getId(), null, TicketStatus.ABIERTO, command.requesterId(), "Ticket creado", now));

        // Si el despacho no responde o no hay técnico elegible, el ticket queda ABIERTO.
        return assignTicket.autoAssign(saved.getId()).orElse(saved);
    }
}