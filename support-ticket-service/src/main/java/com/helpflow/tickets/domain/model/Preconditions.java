package com.helpflow.tickets.domain.model;

import com.helpflow.tickets.domain.exception.DomainException;

final class Preconditions {

    private Preconditions() {
    }

    static void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
    }

    static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new DomainException(message);
        }
        return value;
    }
}