package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.service.SlaPolicy;
import java.util.EnumMap;
import java.util.Map;

public record SlaPolicyResponse(
        Map<TicketPriority, Long> durationHours
) {
    public static SlaPolicyResponse fromDomain(SlaPolicy policy) {
        Map<TicketPriority, Long> map = new EnumMap<>(TicketPriority.class);
        for (TicketPriority p : TicketPriority.values()) {
            map.put(p, policy.durationFor(p).toHours());
        }
        return new SlaPolicyResponse(map);
    }
}
