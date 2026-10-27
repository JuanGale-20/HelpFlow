package com.helpflow.tickets.domain.service;

import static com.helpflow.tickets.domain.model.TicketStatus.ABIERTO;
import static com.helpflow.tickets.domain.model.TicketStatus.ASIGNADO;
import static com.helpflow.tickets.domain.model.TicketStatus.CANCELADO;
import static com.helpflow.tickets.domain.model.TicketStatus.CERRADO;
import static com.helpflow.tickets.domain.model.TicketStatus.EN_PROGRESO;
import static com.helpflow.tickets.domain.model.TicketStatus.RESUELTO;

import com.helpflow.tickets.domain.exception.InvalidTicketStateException;
import com.helpflow.tickets.domain.model.TicketStatus;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class TicketTransitionPolicy {

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED = new EnumMap<>(TicketStatus.class);

    static {
        ALLOWED.put(ABIERTO, EnumSet.of(ASIGNADO, CANCELADO));
        ALLOWED.put(ASIGNADO, EnumSet.of(EN_PROGRESO, CANCELADO));
        ALLOWED.put(EN_PROGRESO, EnumSet.of(RESUELTO));
        ALLOWED.put(RESUELTO, EnumSet.of(CERRADO, EN_PROGRESO));
        ALLOWED.put(CERRADO, EnumSet.noneOf(TicketStatus.class));
        ALLOWED.put(CANCELADO, EnumSet.noneOf(TicketStatus.class));
    }

    public boolean canTransition(TicketStatus from, TicketStatus to) {
        return ALLOWED.get(from).contains(to);
    }

    public void validate(TicketStatus from, TicketStatus to) {
        if (!canTransition(from, to)) {
            throw new InvalidTicketStateException("Transición no permitida: " + from + " -> " + to);
        }
    }
}