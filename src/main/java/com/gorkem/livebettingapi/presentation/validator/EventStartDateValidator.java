package com.gorkem.livebettingapi.presentation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EventStartDateValidator implements ConstraintValidator<ValidStartDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) return false;
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime minValidTime = now.plusMinutes(30);
        return value.isAfter(minValidTime);
    }
}
