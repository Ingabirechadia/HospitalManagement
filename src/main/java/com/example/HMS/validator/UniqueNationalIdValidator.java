package com.example.HMS.validator;

import com.example.HMS.repository.PatientRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueNationalIdValidator implements ConstraintValidator<UniqueNationalId, String> {

    private final PatientRepository patientRepository;

    @Override
    public boolean isValid(String nationalId, ConstraintValidatorContext context) {
        if (nationalId == null || nationalId.isEmpty()) {
            return true;
        }
        return !patientRepository.existsByNationalId(nationalId);
    }
}

