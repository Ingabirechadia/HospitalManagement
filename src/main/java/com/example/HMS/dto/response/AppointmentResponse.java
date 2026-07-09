// src/main/java/com/example/HMS/dto/response/AppointmentResponse.java

package com.example.HMS.dto.response;

import com.example.HMS.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private PatientResponse patient;
    private DoctorResponse doctor;
    private MedicalServiceResponse service;
    private String symptoms;
    private String reasonForVisit;
}