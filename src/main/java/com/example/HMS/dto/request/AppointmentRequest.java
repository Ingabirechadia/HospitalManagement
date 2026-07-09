package com.example.HMS.dto.request;

import com.example.HMS.validator.FutureDate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {

    @NotNull(message = "Appointment date is required")
    @FutureDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @Size(min = 10, max = 500, message = "Symptoms must be between 10 and 500 characters")
    private String symptoms;

    @Size(min = 5, max = 200, message = "Reason must be between 5 and 200 characters")
    private String reasonForVisit;
}