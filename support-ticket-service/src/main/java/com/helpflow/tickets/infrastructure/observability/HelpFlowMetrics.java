package com.helpflow.tickets.infrastructure.observability;

import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class HelpFlowMetrics {

    private final MeterRegistry meterRegistry;
    private final Timer firstResponseTimer;
    private final Timer resolutionTimer;
    private final Counter authFailureCounter;
    private final Counter accessDeniedCounter;

    public HelpFlowMetrics(MeterRegistry meterRegistry, TicketRepositoryPort ticketRepository, Clock clock) {
        this.meterRegistry = meterRegistry;

        this.firstResponseTimer = Timer.builder("helpflow.tickets.first_response_time")
                .description("Tiempo transcurrido desde la creación hasta la primera respuesta")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        this.resolutionTimer = Timer.builder("helpflow.tickets.resolution_time")
                .description("Tiempo transcurrido desde la creación hasta la resolución del ticket")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        this.authFailureCounter = Counter.builder("helpflow.security.auth_failures")
                .description("Número de fallos de autenticación")
                .register(meterRegistry);

        this.accessDeniedCounter = Counter.builder("helpflow.security.access_denied")
                .description("Número de accesos denegados a tickets o recursos")
                .register(meterRegistry);

        Gauge.builder("helpflow.tickets.overdue", () -> {
            Instant now = clock.instant();
            return ticketRepository.findWithSlaRunning().stream()
                    .filter(t -> t.isOverdue(now))
                    .count();
        })
        .description("Número de tickets con SLA vencido en tiempo real")
        .register(meterRegistry);
    }

    public void recordTicketCreated(TicketCategory category, TicketPriority priority) {
        Counter.builder("helpflow.tickets.created")
                .description("Tickets creados por categoría y prioridad")
                .tag("category", category.name())
                .tag("priority", priority.name())
                .register(meterRegistry)
                .increment();
    }

    public void recordFirstResponse(Duration duration) {
        if (duration != null && !duration.isNegative()) {
            firstResponseTimer.record(duration);
        }
    }

    public void recordResolution(Duration duration) {
        if (duration != null && !duration.isNegative()) {
            resolutionTimer.record(duration);
        }
    }

    public void recordAuthFailure() {
        authFailureCounter.increment();
    }

    public void recordAccessDenied() {
        accessDeniedCounter.increment();
    }
}
