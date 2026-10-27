package com.helpflow.tickets.application.port.input;

public interface EscalateTicketsUseCase {

    /** Escala los tickets próximos a incumplir el SLA y devuelve cuántos escaló. */
    int escalateAtRisk();
}
