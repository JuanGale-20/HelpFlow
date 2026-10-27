package com.helpflow.tickets.application.usecase;

import com.helpflow.tickets.application.exception.ResourceNotFoundException;
import com.helpflow.tickets.application.port.input.ChangeStatusCommand;
import com.helpflow.tickets.application.port.input.ManageTicketUseCase;
import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.domain.exception.DomainException;
import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketStatus;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManageTicketService implements ManageTicketUseCase {

    private final TicketRepositoryPort ticketRepository;
    private final HistoryRepositoryPort historyRepository;
    private final TicketAccessChecker accessChecker;
    private final Clock clock;

    public ManageTicketService(TicketRepositoryPort ticketRepository,
                               HistoryRepositoryPort historyRepository,
                               TicketAccessChecker accessChecker,
                               Clock clock) {
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.accessChecker = accessChecker;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Ticket changeStatus(ChangeStatusCommand command) {
        Ticket ticket = ticketRepository.findById(command.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el ticket " + command.ticketId().value()));

        accessChecker.ensureCanChangeStatus(ticket, command.newStatus(), command.actor());
        requireObservation(command);

        Instant now = clock.instant();
        TicketStatus previous = ticket.getStatus();
        ticket.changeStatus(command.newStatus(), now);
        Ticket saved = ticketRepository.save(ticket);

        historyRepository.save(AssignmentHistory.of(
                saved.getId(), previous, saved.getStatus(),
                command.actor().userId(), command.observation(), now));
        return saved;
    }

    /** Resolver y cancelar exigen una observación; los demás cambios la aceptan opcional. */
    private void requireObservation(ChangeStatusCommand command) {
        boolean required = command.newStatus() == TicketStatus.RESUELTO
                || command.newStatus() == TicketStatus.CANCELADO;
        if (required && (command.observation() == null || command.observation().isBlank())) {
            throw new DomainException("La observación es obligatoria al pasar a " + command.newStatus());
        }
    }
}
