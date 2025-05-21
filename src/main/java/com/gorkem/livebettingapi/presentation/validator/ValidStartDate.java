package com.gorkem.livebettingapi.presentation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventStartDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStartDate {
    String message() default "Invalid event start date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
