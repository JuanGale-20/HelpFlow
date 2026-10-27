package com.helpflow.tickets.application.usecase;

import com.helpflow.tickets.application.exception.ResourceNotFoundException;
import com.helpflow.tickets.application.port.input.AssignTicketCommand;
import com.helpflow.tickets.application.port.input.AssignTicketUseCase;
import com.helpflow.tickets.application.port.output.AssignmentRepositoryPort;
import com.helpflow.tickets.application.port.output.DispatchSuggestion;
import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.application.port.output.TechnicianDispatchPort;
import com.helpflow.tickets.application.port.output.TechnicianRepositoryPort;
import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.domain.model.Assignment;
import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.model.Availability;
import com.helpflow.tickets.domain.model.Technician;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.domain.service.AssignmentPolicy;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignTicketService implements AssignTicketUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssignTicketService.class);

    private final TicketRepositoryPort ticketRepository;
    private final TechnicianRepositoryPort technicianRepository;
    private final AssignmentRepositoryPort assignmentRepository;
    private final HistoryRepositoryPort historyRepository;
    private final TechnicianDispatchPort dispatchPort;
    private final AssignmentPolicy assignmentPolicy;
    private final Clock clock;

    public AssignTicketService(TicketRepositoryPort ticketRepository,
                               TechnicianRepositoryPort technicianRepository,
                               AssignmentRepositoryPort assignmentRepository,
                               HistoryRepositoryPort historyRepository,
                               TechnicianDispatchPort dispatchPort,
                               Clock clock) {
        this(ticketRepository, technicianRepository, assignmentRepository,
                historyRepository, dispatchPort, new AssignmentPolicy(), clock);
    }

    @Autowired
    public AssignTicketService(TicketRepositoryPort ticketRepository,
                               TechnicianRepositoryPort technicianRepository,
                               AssignmentRepositoryPort assignmentRepository,
                               HistoryRepositoryPort historyRepository,
                               TechnicianDispatchPort dispatchPort,
                               AssignmentPolicy assignmentPolicy,
                               Clock clock) {
        this.ticketRepository = ticketRepository;
        this.technicianRepository = technicianRepository;
        this.assignmentRepository = assignmentRepository;
        this.historyRepository = historyRepository;
        this.dispatchPort = dispatchPort;
        this.assignmentPolicy = assignmentPolicy;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Optional<Ticket> autoAssign(TicketId ticketId) {
        Ticket ticket = loadTicket(ticketId);

        Optional<DispatchSuggestion> suggestion =
                dispatchPort.suggest(ticketId, ticket.getCategory(), ticket.getPriority());

        Optional<Technician> suggested = suggestion
                .flatMap(s -> technicianRepository.findByCode(s.technicianCode()))
                .filter(t -> assignmentPolicy.isEligible(
                        ticket, t, ticketRepository.countActiveByTechnician(t.id())));

        if (suggested.isPresent()) {
            return Optional.of(applyAssignment(ticket, suggested.get(), suggestion.get(), null));
        }

        Optional<Technician> fallback = findBestCandidate(ticket);
        if (fallback.isEmpty()) {
            log.warn("No hay técnico elegible para el ticket {}; queda ABIERTO", ticketId.value());
            return Optional.empty();
        }
        return Optional.of(applyAssignment(ticket, fallback.get(), null, null,
                "Asignación por reglas internas: el técnico sugerido no estaba disponible o no era elegible"));
    }

    @Override
    @Transactional
    public Ticket assignManually(AssignTicketCommand command) {
        Ticket ticket = loadTicket(command.ticketId());
        Technician technician = technicianRepository.findById(command.technicianId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el técnico " + command.technicianId().value()));

        long activeCount = ticketRepository.countActiveByTechnician(technician.id());
        assignmentPolicy.validate(ticket, technician, activeCount);

        String observation = (command.observation() == null || command.observation().isBlank())
                ? "Asignación manual"
                : command.observation();

        return applyAssignment(ticket, technician, null, command.actorId(), observation);
    }

    private Optional<Technician> findBestCandidate(Ticket ticket) {
        return technicianRepository.findAllActive().stream()
                .map(t -> new Candidate(t, ticketRepository.countActiveByTechnician(t.id())))
                .filter(c -> assignmentPolicy.isEligible(ticket, c.technician(), c.load()))
                .min(Comparator
                        .comparing((Candidate c) -> !c.technician().site().equalsIgnoreCase(ticket.getSite()))
                        .thenComparingLong(Candidate::load))
                .map(Candidate::technician);
    }

    private Ticket applyAssignment(Ticket ticket, Technician technician, DispatchSuggestion suggestion, UserId actorId) {
        String observation = suggestion != null
                ? "Asignación automática por servicio de despacho (" + suggestion.technicianCode() + ")"
                : "Asignación";
        return applyAssignment(ticket, technician, suggestion, actorId, observation);
    }

    private Ticket applyAssignment(Ticket ticket, Technician technician, DispatchSuggestion suggestion,
                                   UserId actorId, String observation) {
        Instant now = clock.instant();
        TicketStatus previous = ticket.getStatus();
        if (previous == TicketStatus.ABIERTO) {
            ticket.assignTo(technician.id());
        } else {
            ticket.reassignTo(technician.id());
        }
        Ticket saved = ticketRepository.save(ticket);

        Integer hours = suggestion != null ? suggestion.estimatedHours() : null;
        Availability availability = suggestion != null ? suggestion.availability() : null;
        assignmentRepository.save(new Assignment(null, saved.getId(), technician.id(), now, hours, availability));

        historyRepository.save(AssignmentHistory.of(
                saved.getId(), previous, saved.getStatus(), actorId, observation, now));

        return saved;
    }

    private Ticket loadTicket(TicketId ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el ticket " + ticketId.value()));
    }

    private record Candidate(Technician technician, long load) {
    }
}
