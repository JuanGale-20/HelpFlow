package com.helpflow.tickets.infrastructure.adapter.in.rest.security;

import com.helpflow.tickets.application.exception.TicketAccessDeniedException;
import com.helpflow.tickets.application.port.input.Actor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityActorResolver {

    public Actor resolveCurrentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new TicketAccessDeniedException("No se encontró un usuario autenticado en la sesión");
        }
        return new Actor(principal.getUserId(), principal.getRole());
    }
}
