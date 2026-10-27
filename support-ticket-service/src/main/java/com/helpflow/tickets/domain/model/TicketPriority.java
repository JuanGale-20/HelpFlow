package com.helpflow.tickets.domain.model;

public enum TicketPriority {
    BAJA, MEDIA, ALTA, CRITICA;

    public boolean isMax() {
        return this == CRITICA;
    }

    /** Siguiente prioridad; CRITICA no sube más. */
    public TicketPriority escalate() {
        return isMax() ? this : values()[ordinal() + 1];
    }
}