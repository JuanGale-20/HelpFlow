package com.helpflow.tickets.infrastructure.observability;

import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketStatus;
import java.time.Duration;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TicketMetricsAspect {

    private final HelpFlowMetrics metrics;

    public TicketMetricsAspect(HelpFlowMetrics metrics) {
        this.metrics = metrics;
    }

    @AfterReturning(
            pointcut = "execution(* com.helpflow.tickets.application.port.input.CreateTicketUseCase.create(..))",
            returning = "ticket"
    )
    public void afterTicketCreated(Ticket ticket) {
        if (ticket != null) {
            metrics.recordTicketCreated(ticket.getCategory(), ticket.getPriority());
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.helpflow.tickets.application.port.input.ManageTicketUseCase.changeStatus(..))",
            returning = "ticket"
    )
    public void afterStatusChanged(Ticket ticket) {
        if (ticket == null) return;

        if (ticket.getStatus() == TicketStatus.EN_PROGRESO && ticket.getFirstResponseAt() != null) {
            Duration elapsed = Duration.between(ticket.getCreatedAt(), ticket.getFirstResponseAt());
            metrics.recordFirstResponse(elapsed);
        }

        if (ticket.getStatus() == TicketStatus.RESUELTO && ticket.getResolvedAt() != null) {
            Duration elapsed = Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt());
            metrics.recordResolution(elapsed);
        }
    }
}
