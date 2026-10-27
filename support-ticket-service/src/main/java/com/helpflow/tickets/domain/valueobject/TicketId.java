package com.helpflow.tickets.domain.valueobject;

import java.util.Objects;

public record TicketId(Long value) {

    public TicketId {
        Objects.requireNonNull(value, "El id del ticket es obligatorio");
    }
}
