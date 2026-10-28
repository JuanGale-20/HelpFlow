package com.helpflow.tickets.infrastructure.adapter.in.rest.validation;

import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.ChangeStatusRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusChangeValidator implements ConstraintValidator<ValidStatusChange, ChangeStatusRequest> {

    @Override
    public boolean isValid(ChangeStatusRequest request, ConstraintValidatorContext context) {
        if (request == null || request.status() == null) {
            return true; // @NotNull ya lo cubre
        }
        boolean requiresObservation = request.status() == TicketStatus.RESUELTO
                || request.status() == TicketStatus.CANCELADO;
        boolean missing = request.observation() == null || request.observation().isBlank();

        if (requiresObservation && missing) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "La observación es obligatoria al pasar a " + request.status())
                    .addPropertyNode("observation")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
