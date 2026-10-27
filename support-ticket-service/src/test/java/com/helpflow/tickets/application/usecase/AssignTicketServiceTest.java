package com.helpflow.tickets.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.helpflow.tickets.application.port.input.AssignTicketCommand;
import com.helpflow.tickets.application.port.output.AssignmentRepositoryPort;
import com.helpflow.tickets.application.port.output.DispatchSuggestion;
import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.application.port.output.TechnicianDispatchPort;
import com.helpflow.tickets.application.port.output.TechnicianRepositoryPort;
import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.domain.exception.AssignmentNotAllowedException;
import com.helpflow.tickets.domain.model.Availability;
import com.helpflow.tickets.domain.model.Technician;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.domain.model.UserStatus;
import com.helpflow.tickets.domain.service.AssignmentPolicy;
import com.helpflow.tickets.domain.valueobject.SlaDeadline;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssignTicketServiceTest {

    @Mock private TicketRepositoryPort ticketRepository;
    @Mock private TechnicianRepositoryPort technicianRepository;
    @Mock private AssignmentRepositoryPort assignmentRepository;
    @Mock private HistoryRepositoryPort historyRepository;
    @Mock private TechnicianDispatchPort dispatchPort;

    private Clock clock;
    private AssignTicketService assignTicketService;

    private final Instant now = Instant.parse("2026-10-27T10:00:00Z");
    private final TicketId ticketId = new TicketId(10L);
    private final TechnicianId tecId1 = new TechnicianId(1L);
    private final TechnicianId tecId2 = new TechnicianId(2L);

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(now, ZoneId.of("UTC"));
        assignTicketService = new AssignTicketService(
                ticketRepository, technicianRepository, assignmentRepository,
                historyRepository, dispatchPort, new AssignmentPolicy(), clock);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Ticket createOpenTicket(TicketCategory category, String site) {
        return new Ticket(ticketId, new UserId(100L), "Problema", "Detalle",
                category, TicketPriority.ALTA, site, "Asset-1",
                now, new SlaDeadline(now.plusSeconds(3600)), TicketStatus.ABIERTO,
                null, null, null, 0);
    }

    @Test
    @DisplayName("Auto-asignación con técnico sugerido por dispatch si es elegible")
    void autoAssignConTecnicoSugerido() {
        Ticket ticket = createOpenTicket(TicketCategory.RED, "Sede Central");
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        DispatchSuggestion suggestion = new DispatchSuggestion("TEC-009", Availability.ALTA, 2);
        when(dispatchPort.suggest(ticketId, TicketCategory.RED, TicketPriority.ALTA))
                .thenReturn(Optional.of(suggestion));

        Technician tec = new Technician(tecId1, "TEC-009", new UserId(2L), "Felipe Ortiz",
                TicketCategory.RED, "Sede Central", 5, UserStatus.ACTIVO);
        when(technicianRepository.findByCode("TEC-009")).thenReturn(Optional.of(tec));
        when(ticketRepository.countActiveByTechnician(tecId1)).thenReturn(1L);

        Optional<Ticket> result = assignTicketService.autoAssign(ticketId);

        assertTrue(result.isPresent());
        assertEquals(TicketStatus.ASIGNADO, result.get().getStatus());
        assertEquals(tecId1, result.get().getAssignedTechnicianId());
        verify(assignmentRepository).save(any());
        verify(historyRepository).save(any());
    }

    @Test
    @DisplayName("Auto-asignación por fallback de reglas internas si el sugerido no es elegible")
    void autoAssignFallbackReglasInternas() {
        Ticket ticket = createOpenTicket(TicketCategory.HARDWARE, "Sede Norte");
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Dispatch no devuelve sugerencia
        when(dispatchPort.suggest(any(), any(), any())).thenReturn(Optional.empty());

        // Candidato 1: Sede Sur, carga 0
        Technician tecSur = new Technician(tecId1, "TEC-003", new UserId(4L), "Andrés",
                TicketCategory.HARDWARE, "Sede Sur", 5, UserStatus.ACTIVO);
        // Candidato 2: Sede Norte (misma sede), carga 1
        Technician tecNorte = new Technician(tecId2, "TEC-002", new UserId(3L), "Laura",
                TicketCategory.HARDWARE, "Sede Norte", 5, UserStatus.ACTIVO);

        when(technicianRepository.findAllActive()).thenReturn(List.of(tecSur, tecNorte));
        when(ticketRepository.countActiveByTechnician(tecId1)).thenReturn(0L);
        when(ticketRepository.countActiveByTechnician(tecId2)).thenReturn(1L);

        Optional<Ticket> result = assignTicketService.autoAssign(ticketId);

        assertTrue(result.isPresent());
        // Debe priorizar misma sede aunque tenga 1 ticket de carga
        assertEquals(tecId2, result.get().getAssignedTechnicianId());
    }

    @Test
    @DisplayName("Auto-asignación devuelve vacío si ningún técnico es elegible")
    void autoAssignSinCandidatos() {
        Ticket ticket = createOpenTicket(TicketCategory.SOFTWARE, "Sede Central");
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(dispatchPort.suggest(any(), any(), any())).thenReturn(Optional.empty());
        when(technicianRepository.findAllActive()).thenReturn(List.of());

        Optional<Ticket> result = assignTicketService.autoAssign(ticketId);

        assertTrue(result.isEmpty());
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Asignación manual por admin con validación exitosa")
    void assignManuallyExitoso() {
        Ticket ticket = createOpenTicket(TicketCategory.SOFTWARE, "Sede Central");
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        Technician tec = new Technician(tecId1, "TEC-005", new UserId(6L), "Javier",
                TicketCategory.SOFTWARE, "Sede Central", 5, UserStatus.ACTIVO);
        when(technicianRepository.findById(tecId1)).thenReturn(Optional.of(tec));
        when(ticketRepository.countActiveByTechnician(tecId1)).thenReturn(2L);

        AssignTicketCommand command = new AssignTicketCommand(
                ticketId, tecId1, new UserId(1L), "Asignación forzada por urgencia");

        Ticket result = assignTicketService.assignManually(command);

        assertEquals(TicketStatus.ASIGNADO, result.getStatus());
        assertEquals(tecId1, result.getAssignedTechnicianId());
    }

    @Test
    @DisplayName("Asignación manual rechazada si el técnico superó su carga máxima")
    void assignManuallyRechazadaPorCarga() {
        Ticket ticket = createOpenTicket(TicketCategory.SOFTWARE, "Sede Central");
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        Technician tec = new Technician(tecId1, "TEC-005", new UserId(6L), "Javier",
                TicketCategory.SOFTWARE, "Sede Central", 3, UserStatus.ACTIVO);
        when(technicianRepository.findById(tecId1)).thenReturn(Optional.of(tec));
        when(ticketRepository.countActiveByTechnician(tecId1)).thenReturn(3L); // Lleno

        AssignTicketCommand command = new AssignTicketCommand(ticketId, tecId1, new UserId(1L), null);

        assertThrows(AssignmentNotAllowedException.class, () -> assignTicketService.assignManually(command));
    }
}
