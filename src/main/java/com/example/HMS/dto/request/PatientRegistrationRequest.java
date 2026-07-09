package com.example.HMS.dto.request;

import com.example.HMS.enums.Gender;
import com.example.HMS.validator.PastDate;
import com.example.HMS.validator.UniqueEmail;
import com.example.HMS.validator.UniqueNationalId;
import com.example.HMS.validator.ValidPhoneNumber;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class PatientRegistrationRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    @PastDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @UniqueEmail
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain at least one letter, one number, and one special character")
    private String password;

    @NotBlank(message = "Phone number is required")
    @ValidPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @UniqueNationalId
    @Pattern(regexp = "^[A-Z0-9]{6,20}$", message = "National ID must be 6-20 alphanumeric characters")
    private String nationalId;

    @NotBlank(message = "Emergency contact name is required")
    private String emergencyContactName;

    @NotBlank(message = "Emergency contact phone is required")
    @ValidPhoneNumber
    private String emergencyContactPhone;
}