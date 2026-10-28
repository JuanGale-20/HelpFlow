package com.helpflow.tickets.infrastructure.adapter.in.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SlaHoursValidator.class)
public @interface ValidSlaHours {
    String message() default "Configuración de horas de SLA inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
