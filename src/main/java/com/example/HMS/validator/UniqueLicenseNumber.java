package com.example.HMS.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueLicenseNumberValidator.class)
@Documented
public @interface UniqueLicenseNumber {
    String message() default "Medical license number already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}