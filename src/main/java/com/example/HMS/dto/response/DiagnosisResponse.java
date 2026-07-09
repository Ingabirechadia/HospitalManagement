// src/main/java/com/example/HMS/dto/response/DiagnosisResponse.java

package com.example.HMS.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisResponse {
    private Long id;
    private Long appointmentId;
    private DoctorResponse doctor;
    private String symptoms;
    private String diagnosisNotes;
    private String recommendedTreatment;
    private Boolean readOnly;
    private LocalDate diagnosisDate;
}