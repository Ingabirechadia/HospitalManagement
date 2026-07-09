package com.example.HMS.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PatchPatientRequest {

    private String address;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String emergencyContactName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String emergencyContactPhone;
}