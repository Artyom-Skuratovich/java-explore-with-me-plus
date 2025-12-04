package ru.practicum.ewm.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class MinHoursFromNowValidator implements ConstraintValidator<MinHoursFromNow, LocalDateTime> {
    private int hours;

    @Override
    public void initialize(MinHoursFromNow constraintAnnotation) {
        hours = constraintAnnotation.hours();
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && !value.isBefore(LocalDateTime.now().plusHours(hours));
    }
}