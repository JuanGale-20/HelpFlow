package com.helpflow.tickets.domain.service;

import com.helpflow.tickets.domain.exception.DomainException;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.valueobject.SlaDeadline;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

public class SlaPolicy {

    /** Un ticket se considera próximo a incumplir cuando consumió esta fracción de su tiempo de SLA. */
    public static final double ESCALATION_THRESHOLD = 0.8;

    private final Map<TicketPriority, Duration> durations;

    public SlaPolicy(Map<TicketPriority, Duration> durations) {
        for (TicketPriority priority : TicketPriority.values()) {
            Duration duration = durations.get(priority);
            if (duration == null || duration.isZero() || duration.isNegative()) {
                throw new DomainException("Falta un SLA válido para la prioridad " + priority);
            }
        }
        this.durations = new EnumMap<>(durations);
    }

    public static SlaPolicy defaultPolicy() {
        return new SlaPolicy(Map.of(
                TicketPriority.CRITICA, Duration.ofHours(4),
                TicketPriority.ALTA, Duration.ofHours(8),
                TicketPriority.MEDIA, Duration.ofHours(24),
                TicketPriority.BAJA, Duration.ofHours(72)));
    }

    public Duration durationFor(TicketPriority priority) {
        return durations.get(priority);
    }

    public SlaDeadline calculateDeadline(TicketPriority priority, Instant from) {
        return new SlaDeadline(from.plus(durations.get(priority)));
    }
}