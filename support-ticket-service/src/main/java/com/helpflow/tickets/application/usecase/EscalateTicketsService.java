package com.helpflow.tickets.application.usecase;

import com.helpflow.tickets.application.port.input.EscalateTicketsUseCase;
import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketPriority;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EscalateTicketsService implements EscalateTicketsUseCase {

    private static final Logger log = LoggerFactory.getLogger(EscalateTicketsService.class);

    private final TicketRepositoryPort ticketRepository;
    private final HistoryRepositoryPort historyRepository;
    private final Clock clock;

    public EscalateTicketsService(TicketRepositoryPort ticketRepository,
                                  HistoryRepositoryPort historyRepository,
                                  Clock clock) {
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public int escalateAtRisk() {
        Instant now = clock.instant();
        List<Ticket> candidates = ticketRepository.findWithSlaRunning().stream()
                .filter(ticket -> ticket.canBeEscalated(now))
                .toList();

        for (Ticket ticket : candidates) {
            TicketPriority previous = ticket.getPriority();
            ticket.escalate();
            Ticket saved = ticketRepository.save(ticket);
            historyRepository.save(AssignmentHistory.of(
                    saved.getId(), saved.getStatus(), saved.getStatus(), null,
                    "Escalado automático por SLA: prioridad " + previous + " -> " + saved.getPriority(),
                    now));
            log.info("Ticket {} escalado de {} a {}", saved.getId().value(), previous, saved.getPriority());
        }
        return candidates.size();
    }
}
