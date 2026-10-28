package com.helpflow.tickets.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "La sede es obligatoria") String site,
        @NotBlank(message = "El área es obligatoria") String area
) {
}
