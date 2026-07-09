package com.example.HMS.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class HospitalUpdateRequest {

    private String name;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String telephone;

    private String physicalAddress;

    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid website URL format")
    private String website;

    @Email(message = "Invalid email format")
    private String email;

    private Boolean active;
}