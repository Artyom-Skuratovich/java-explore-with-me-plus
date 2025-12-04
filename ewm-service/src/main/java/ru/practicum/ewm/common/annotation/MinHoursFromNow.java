package ru.practicum.ewm.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Positive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {MinHoursFromNowValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinHoursFromNow {
    String message() default "The date must be not earlier than {hours} hours from now";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Positive(message = "Hours must be a positive number")
    int hours();
}