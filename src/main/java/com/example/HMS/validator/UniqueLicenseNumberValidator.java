package com.example.HMS.validator;

import com.example.HMS.repository.DoctorRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueLicenseNumberValidator implements ConstraintValidator<UniqueLicenseNumber, String> {

    private final DoctorRepository doctorRepository;

    @Override
    public boolean isValid(String licenseNumber, ConstraintValidatorContext context) {
        if (licenseNumber == null || licenseNumber.isEmpty()) {
            return true;
        }
        return !doctorRepository.existsByMedicalLicenseNumber(licenseNumber);
    }
}