package com.helpflow.tickets.infrastructure.adapter.in.rest.validation;

import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TechnicianDataValidator implements ConstraintValidator<ValidTechnicianData, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request == null || request.role() != Role.ROLE_TECNICO) {
            return true;
        }
        boolean valid = true;
        context.disableDefaultConstraintViolation();
        if (request.technicianCode() == null || request.technicianCode().isBlank()) {
            context.buildConstraintViolationWithTemplate("El código de técnico es obligatorio para ROLE_TECNICO")
                    .addPropertyNode("technicianCode").addConstraintViolation();
            valid = false;
        }
        if (request.specialty() == null) {
            context.buildConstraintViolationWithTemplate("La especialidad es obligatoria para ROLE_TECNICO")
                    .addPropertyNode("specialty").addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
