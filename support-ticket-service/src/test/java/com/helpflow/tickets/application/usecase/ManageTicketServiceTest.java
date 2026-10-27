package com.helpflow.tickets.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.helpflow.tickets.application.exception.TicketAccessDeniedException;
import com.helpflow.tickets.application.port.input.Actor;
import com.helpflow.tickets.application.port.input.ChangeStatusCommand;
import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.application.port.output.TechnicianRepositoryPort;
import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.domain.exception.DomainException;
import com.helpflow.tickets.domain.exception.InvalidTicketStateException;
import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.model.Technician;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.domain.model.UserStatus;
import com.helpflow.tickets.domain.valueobject.SlaDeadline;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
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
class ManageTicketServiceTest {

    @Mock private TicketRepositoryPort ticketRepository;
    @Mock private HistoryRepositoryPort historyRepository;
    @Mock private TechnicianRepositoryPort technicianRepository;

    private ManageTicketService manageTicketService;
    private final Instant now = Instant.parse("2026-10-27T10:00:00Z");
    private final TicketId ticketId = new TicketId(50L);
    private final UserId requesterId = new UserId(10L);
    private final UserId techUserId = new UserId(20L);
    private final TechnicianId techId = new TechnicianId(5L);

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        TicketAccessChecker accessChecker = new TicketAccessChecker(technicianRepository);
        manageTicketService = new ManageTicketService(ticketRepository, historyRepository, accessChecker, clock);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Ticket createTicket(TicketStatus status, TechnicianId assignedTech) {
        return new Ticket(ticketId, requesterId, "Error login", "No entra",
                TicketCategory.ACCESOS, TicketPriority.MEDIA, "Sede Norte", "PC-10",
                now, new SlaDeadline(now.plusSeconds(7200)), status, assignedTech,
                null, null, 0);
    }

    @Test
    @DisplayName("Usuario cancela su propio ticket con observación")
    void cancelarTicketPorUsuario() {
        Ticket ticket = createTicket(TicketStatus.ABIERTO, null);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        Actor actor = new Actor(requesterId, Role.ROLE_USUARIO);
        ChangeStatusCommand command = new ChangeStatusCommand(
                ticketId, TicketStatus.CANCELADO, actor, "Ya no lo necesito");

        Ticket result = manageTicketService.changeStatus(command);

        assertEquals(TicketStatus.CANCELADO, result.getStatus());
        verify(historyRepository).save(any());
    }

    @Test
    @DisplayName("Cancelar ticket sin observación lanza DomainException")
    void cancelarSinObservacionFalla() {
        Ticket ticket = createTicket(TicketStatus.ABIERTO, null);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        Actor actor = new Actor(requesterId, Role.ROLE_USUARIO);
        ChangeStatusCommand command = new ChangeStatusCommand(
                ticketId, TicketStatus.CANCELADO, actor, "  "); // blanco

        assertThrows(DomainException.class, () -> manageTicketService.changeStatus(command));
    }

    @Test
    @DisplayName("Técnico asignado cambia estado a EN_PROGRESO")
    void tecnicoAvanzaAEnProgreso() {
        Ticket ticket = createTicket(TicketStatus.ASIGNADO, techId);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        Technician tec = new Technician(techId, "TEC-014", techUserId, "Natalia",
                TicketCategory.ACCESOS, "Sede Norte", 5, UserStatus.ACTIVO);
        when(technicianRepository.findByUserId(techUserId)).thenReturn(Optional.of(tec));

        Actor actor = new Actor(techUserId, Role.ROLE_TECNICO);
        ChangeStatusCommand command = new ChangeStatusCommand(
                ticketId, TicketStatus.EN_PROGRESO, actor, "Iniciando diagnóstico");

        Ticket result = manageTicketService.changeStatus(command);

        assertEquals(TicketStatus.EN_PROGRESO, result.getStatus());
        assertNotNull(result.getFirstResponseAt());
    }

    @Test
    @DisplayName("Usuario ajeno recibe TicketAccessDeniedException")
    void usuarioAjenoNoPuedeCambiarEstado() {
        Ticket ticket = createTicket(TicketStatus.ABIERTO, null);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        UserId otroUsuario = new UserId(999L);
        Actor actor = new Actor(otroUsuario, Role.ROLE_USUARIO);
        ChangeStatusCommand command = new ChangeStatusCommand(
                ticketId, TicketStatus.CANCELADO, actor, "Intento de cancelación ajena");

        assertThrows(TicketAccessDeniedException.class, () -> manageTicketService.changeStatus(command));
    }

    @Test
    @DisplayName("Transición inválida lanza InvalidTicketStateException")
    void transicionInvalidaFalla() {
        Ticket ticket = createTicket(TicketStatus.ABIERTO, null);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Admin tiene permisos de acceso pero la regla de ciclo de vida del ticket no permite ABIERTO -> RESUELTO directo
        Actor admin = new Actor(new UserId(1L), Role.ROLE_ADMIN);
        ChangeStatusCommand command = new ChangeStatusCommand(
                ticketId, TicketStatus.RESUELTO, admin, "Resolución sin pasar por en progreso");

        assertThrows(InvalidTicketStateException.class, () -> manageTicketService.changeStatus(command));
    }
}
