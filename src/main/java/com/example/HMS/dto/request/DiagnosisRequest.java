package com.example.HMS.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DiagnosisRequest {

    @NotBlank(message = "Symptoms are required")
    @Size(min = 10, message = "Symptoms must be at least 10 characters")
    private String symptoms;

    @NotBlank(message = "Diagnosis notes are required")
    @Size(min = 20, message = "Diagnosis notes must be at least 20 characters")
    private String diagnosisNotes;

    @NotBlank(message = "Recommended treatment is required")
    private String recommendedTreatment;

    private MultipartFile prescriptionFile;
}