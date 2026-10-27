package com.helpflow.tickets.domain.valueobject;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record SlaDeadline(Instant value) {

    public SlaDeadline {
        Objects.requireNonNull(value, "La fecha límite de SLA es obligatoria");
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(value);
    }

    /** Fracción del tiempo de SLA consumida entre el inicio y ahora (1.0 = vencido). */
    public double consumedRatio(Instant start, Instant now) {
        long total = Duration.between(start, value).toMillis();
        if (total <= 0) {
            return 1.0;
        }
        long elapsed = Duration.between(start, now).toMillis();
        return Math.max(0.0, (double) elapsed / total);
    }
}