package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.TicketPriority;
import java.time.Duration;
import java.util.Map;

public record ConfigureSlaCommand(
        Map<TicketPriority, Duration> durations,
        Actor actor
) {
}
