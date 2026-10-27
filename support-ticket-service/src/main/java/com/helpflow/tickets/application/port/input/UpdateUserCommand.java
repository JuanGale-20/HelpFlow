package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.valueobject.UserId;

public record UpdateUserCommand(
        UserId userId,
        String name,
        String site,
        String area,
        Actor actor
) {
}
