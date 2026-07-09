// src/main/java/com/example/HMS/dto/request/UpdateDoctorRequest.java

package com.example.HMS.dto.request;

import com.example.HMS.enums.AvailabilityStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.List;

@Data
public class UpdateDoctorRequest {

    private String medicalLicenseNumber;
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String specialization;
    private AvailabilityStatus availabilityStatus;

    @DecimalMin(value = "0.0", inclusive = false, message = "Consultation fee must be greater than 0")
    private Double consultationFee;

    private List<Long> serviceIds;
    private Boolean active;
}