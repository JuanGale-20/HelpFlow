package com.helpflow.tickets.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.helpflow.tickets.application.exception.ResourceNotFoundException;
import com.helpflow.tickets.application.port.input.AssignTicketUseCase;
import com.helpflow.tickets.application.port.input.CreateTicketCommand;
import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.application.port.output.SlaConfigurationPort;
import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.application.port.output.UserRepositoryPort;
import com.helpflow.tickets.domain.exception.DomainException;
import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.domain.model.User;
import com.helpflow.tickets.domain.model.UserStatus;
import com.helpflow.tickets.domain.service.SlaPolicy;
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
class CreateTicketServiceTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private TicketRepositoryPort ticketRepository;
    @Mock private HistoryRepositoryPort historyRepository;
    @Mock private SlaConfigurationPort slaConfiguration;
    @Mock private AssignTicketUseCase assignTicket;

    private Clock clock;
    private CreateTicketService createTicketService;

    private final Instant now = Instant.parse("2026-10-27T10:00:00Z");
    private final UserId requesterId = new UserId(1L);

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(now, ZoneId.of("UTC"));
        when(slaConfiguration.currentPolicy()).thenReturn(SlaPolicy.defaultPolicy());
        createTicketService = new CreateTicketService(
                userRepository, ticketRepository, historyRepository, slaConfiguration, assignTicket, clock);
    }

    @Test
    @DisplayName("Debe crear un ticket en estado ABIERTO y disparar auto-asignación")
    void crearTicketExitoso() {
        User user = new User(requesterId, "12345678", "Juan Pérez", "juan@helpflow.com",
                "hash", "Sede Central", "TI", UserStatus.ACTIVO, Role.ROLE_USUARIO);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(user));

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            return new Ticket(new TicketId(100L), t.getRequesterId(), t.getTitle(), t.getDescription(),
                    t.getCategory(), t.getPriority(), t.getSite(), t.getAffectedAsset(), t.getCreatedAt(),
                    t.getSlaDeadline(), t.getStatus(), t.getAssignedTechnicianId(), t.getFirstResponseAt(),
                    t.getResolvedAt(), t.getEscalationCount());
        });
        when(assignTicket.autoAssign(any())).thenReturn(Optional.empty());

        CreateTicketCommand command = new CreateTicketCommand(
                requesterId, "Falla en VPN", "No conecta", TicketCategory.RED,
                TicketPriority.ALTA, null, "Laptop-01");

        Ticket created = createTicketService.create(command);

        assertNotNull(created);
        assertEquals(new TicketId(100L), created.getId());
        assertEquals("Falla en VPN", created.getTitle());
        assertEquals("Sede Central", created.getSite()); // heredada del usuario si viene nula
        assertEquals(TicketStatus.ABIERTO, created.getStatus());
        verify(historyRepository).save(any());
        verify(assignTicket).autoAssign(any());
    }

    @Test
    @DisplayName("Debe lanzar DomainException si el usuario está INACTIVO")
    void usuarioInactivoNoPuedeCrearTicket() {
        User user = new User(requesterId, "12345678", "Inactivo", "inactivo@helpflow.com",
                "hash", "Sede Central", "TI", UserStatus.INACTIVO, Role.ROLE_USUARIO);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(user));

        CreateTicketCommand command = new CreateTicketCommand(
                requesterId, "Falla en VPN", "No conecta", TicketCategory.RED,
                TicketPriority.ALTA, "Sede Central", "Laptop-01");

        assertThrows(DomainException.class, () -> createTicketService.create(command));
        verifyNoInteractions(ticketRepository);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el usuario no existe")
    void usuarioInexistenteLanzaExcepcion() {
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        CreateTicketCommand command = new CreateTicketCommand(
                requesterId, "Falla", "Desc", TicketCategory.SOFTWARE,
                TicketPriority.BAJA, "Sede Norte", "PC-02");

        assertThrows(ResourceNotFoundException.class, () -> createTicketService.create(command));
    }
}
