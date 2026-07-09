// src/main/java/com/example/HMS/mapper/AppointmentMapper.java

package com.example.HMS.mapper;

import com.example.HMS.dto.request.AppointmentRequest;
import com.example.HMS.dto.response.AppointmentResponse;
import com.example.HMS.entity.Appointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "diagnosis", ignore = true)
    @Mapping(target = "telehealth", ignore = true)
    @Mapping(target = "meetingLink", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Appointment toEntity(AppointmentRequest request);

    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "service", source = "service")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "appointmentDate", source = "appointmentDate")
    @Mapping(target = "appointmentTime", source = "appointmentTime")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "symptoms", source = "symptoms")
    @Mapping(target = "reasonForVisit", source = "reasonForVisit")
    AppointmentResponse toResponse(Appointment appointment);
}