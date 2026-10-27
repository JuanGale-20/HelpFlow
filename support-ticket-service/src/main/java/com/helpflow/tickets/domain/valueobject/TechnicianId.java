package com.helpflow.tickets.domain.valueobject;

import java.util.Objects;

public record TechnicianId(Long value) {

    public TechnicianId {
        Objects.requireNonNull(value, "El id del técnico es obligatorio");
    }
}