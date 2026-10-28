package com.helpflow.tickets.infrastructure.adapter.in.rest.validation;

import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.ConfigureSlaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class SlaHoursValidator implements ConstraintValidator<ValidSlaHours, ConfigureSlaRequest> {

    // De la más urgente a la menos urgente
    private static final List<TicketPriority> ORDER = List.of(
            TicketPriority.CRITICA, TicketPriority.ALTA, TicketPriority.MEDIA, TicketPriority.BAJA);

    @Override
    public boolean isValid(ConfigureSlaRequest request, ConstraintValidatorContext context) {
        if (request == null || request.hours() == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();

        if (request.hours().values().stream().anyMatch(h -> h == null || h <= 0)) {
            context.buildConstraintViolationWithTemplate("Todas las horas de SLA deben ser mayores a 0")
                    .addPropertyNode("hours").addConstraintViolation();
            return false;
        }

        int previous = 0;
        for (TicketPriority priority : ORDER) {
            Integer hours = request.hours().get(priority);
            if (hours == null) continue;
            if (hours < previous) {
                context.buildConstraintViolationWithTemplate(
                                "El SLA de " + priority + " no puede ser menor al de una prioridad más urgente")
                        .addPropertyNode("hours").addConstraintViolation();
                return false;
            }
            previous = hours;
        }
        return true;
    }
}
