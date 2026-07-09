package com.example.HMS.dto.request;

import com.example.HMS.enums.AvailabilityStatus;
import com.example.HMS.enums.Gender;
import com.example.HMS.validator.UniqueEmail;
import com.example.HMS.validator.UniqueLicenseNumber;
import com.example.HMS.validator.ValidPhoneNumber;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class DoctorCreationRequest {

    @NotBlank(message = "Medical license number is required")
    @Pattern(regexp = "^[A-Z0-9]{8,20}$", message = "Medical license must be 8-20 alphanumeric characters")
    @UniqueLicenseNumber
    private String medicalLicenseNumber;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @UniqueEmail
    private String email;

    @NotBlank(message = "Phone number is required")
    @ValidPhoneNumber
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Specialization is required")
    @Size(min = 2, max = 100, message = "Specialization must be between 2 and 100 characters")
    private String specialization;

    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Consultation fee must be greater than 0")
    private Double consultationFee;

    @NotEmpty(message = "At least one service must be assigned")
    private List<Long> serviceIds;
}