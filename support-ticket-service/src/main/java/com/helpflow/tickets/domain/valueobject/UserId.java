package com.helpflow.tickets.domain.valueobject;

import java.util.Objects;

public record UserId(Long value) {

    public UserId {
        Objects.requireNonNull(value, "El id del usuario es obligatorio");
    }
}