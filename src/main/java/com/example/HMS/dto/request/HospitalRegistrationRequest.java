package com.example.HMS.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HospitalRegistrationRequest {

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @NotBlank(message = "Telephone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String telephone;

    @NotBlank(message = "Address is required")
    private String physicalAddress;

    private String website;

    @NotBlank(message = "Administrator full name is required")
    private String adminFullName;

    @NotBlank(message = "Administrator email is required")
    @Email(message = "Invalid email format")
    private String adminEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$",
            message = "Password must contain at least one letter and one number")
    private String adminPassword;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String adminPhoneNumber;
}