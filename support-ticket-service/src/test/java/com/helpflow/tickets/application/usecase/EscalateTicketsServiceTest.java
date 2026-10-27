package com.helpflow.tickets.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.domain.valueobject.SlaDeadline;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
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
class EscalateTicketsServiceTest {

    @Mock private TicketRepositoryPort ticketRepository;
    @Mock private HistoryRepositoryPort historyRepository;

    private EscalateTicketsService escalateTicketsService;
    private final Instant start = Instant.parse("2026-10-27T00:00:00Z");

    @BeforeEach
    void setUp() {
        // Reloj simulando que han transcurrido 9 horas de un SLA de 10 horas (90% consumido >= 80% umbral)
        Instant now = start.plus(Duration.ofHours(9));
        Clock clock = Clock.fixed(now, ZoneId.of("UTC"));
        escalateTicketsService = new EscalateTicketsService(ticketRepository, historyRepository, clock);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Debe escalar tickets próximos a vencer el SLA (prioridad sube de MEDIA a ALTA)")
    void escalarTicketEnRiesgo() {
        // Duración total = 10 horas. Consumido = 9 horas (90%).
        SlaDeadline deadline = new SlaDeadline(start.plus(Duration.ofHours(10)));
        Ticket ticket = new Ticket(new TicketId(1L), new UserId(1L), "Lentitud", "Desc",
                TicketCategory.RED, TicketPriority.MEDIA, "Sede Central", "Switch-1",
                start, deadline, TicketStatus.EN_PROGRESO, null, start.plus(Duration.ofHours(1)), null, 0);

        when(ticketRepository.findWithSlaRunning()).thenReturn(List.of(ticket));

        int escalatedCount = escalateTicketsService.escalateAtRisk();

        assertEquals(1, escalatedCount);
        assertEquals(TicketPriority.ALTA, ticket.getPriority());
        assertEquals(1, ticket.getEscalationCount());
        verify(historyRepository).save(any());
    }

    @Test
    @DisplayName("No debe escalar tickets con menos del 80% consumido")
    void noEscalarTicketLejosDeVencer() {
        // Duración total = 100 horas. Consumido = 9 horas (9%).
        SlaDeadline deadline = new SlaDeadline(start.plus(Duration.ofHours(100)));
        Ticket ticket = new Ticket(new TicketId(2L), new UserId(1L), "Lentitud", "Desc",
                TicketCategory.SOFTWARE, TicketPriority.BAJA, "Sede Central", "PC-2",
                start, deadline, TicketStatus.ABIERTO, null, null, null, 0);

        when(ticketRepository.findWithSlaRunning()).thenReturn(List.of(ticket));

        int escalatedCount = escalateTicketsService.escalateAtRisk();

        assertEquals(0, escalatedCount);
        assertEquals(TicketPriority.BAJA, ticket.getPriority());
        verify(historyRepository, never()).save(any());
    }
}
