package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import com.helpflow.tickets.domain.model.Role;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String name,
        String email,
        Role role
) {
}
