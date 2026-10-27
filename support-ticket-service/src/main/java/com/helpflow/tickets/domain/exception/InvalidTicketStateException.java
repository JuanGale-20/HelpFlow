package com.helpflow.tickets.domain.exception;

public class InvalidTicketStateException extends DomainException {

    public InvalidTicketStateException(String message) {
        super(message);
    }
}