package com.example.HMS.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNationalIdValidator.class)
@Documented
public @interface UniqueNationalId {
    String message() default "National ID already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}