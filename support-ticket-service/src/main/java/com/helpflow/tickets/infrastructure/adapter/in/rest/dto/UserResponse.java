package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.model.UserStatus;

public record UserResponse(
        Long id,
        String document,
        String name,
        String email,
        String site,
        String area,
        UserStatus status,
        Role role
) {
}
