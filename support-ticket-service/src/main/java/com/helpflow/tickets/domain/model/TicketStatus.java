package com.helpflow.tickets.domain.model;

public enum TicketStatus {
    ABIERTO, ASIGNADO, EN_PROGRESO, RESUELTO, CERRADO, CANCELADO;

    public boolean isTerminal() {
        return this == CERRADO || this == CANCELADO;
    }

    /** Estados en los que el ticket cuenta para la carga activa del técnico. */
    public boolean countsAsTechnicianLoad() {
        return this == ASIGNADO || this == EN_PROGRESO;
    }

    /** Estados en los que el reloj del SLA sigue corriendo. */
    public boolean isSlaRunning() {
        return this == ABIERTO || this == ASIGNADO || this == EN_PROGRESO;
    }
}