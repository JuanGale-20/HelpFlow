package com.helpflow.tickets.infrastructure.adapter.in.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TechnicianDataValidator.class)
public @interface ValidTechnicianData {
    String message() default "Datos de técnico incompletos para ROLE_TECNICO";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
