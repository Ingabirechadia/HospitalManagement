// src/main/java/com/example/HMS/dto/response/DoctorResponse.java

package com.example.HMS.dto.response;

import com.example.HMS.enums.AvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String medicalLicenseNumber;
    private String specialization;
    private AvailabilityStatus availabilityStatus;
    private Double consultationFee;
    private Long hospitalId;
    private String hospitalName;
    private List<MedicalServiceResponse> services;
    private Boolean active;
}