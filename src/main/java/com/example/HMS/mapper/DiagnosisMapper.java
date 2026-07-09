// src/main/java/com/example/HMS/mapper/DiagnosisMapper.java

package com.example.HMS.mapper;

import com.example.HMS.dto.request.DiagnosisRequest;
import com.example.HMS.dto.response.DiagnosisResponse;
import com.example.HMS.entity.Diagnosis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiagnosisMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "readOnly", constant = "false")
    @Mapping(target = "diagnosisDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "prescriptions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Diagnosis toEntity(DiagnosisRequest request);

    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "symptoms", source = "symptoms")
    @Mapping(target = "diagnosisNotes", source = "diagnosisNotes")
    @Mapping(target = "recommendedTreatment", source = "recommendedTreatment")
    @Mapping(target = "readOnly", source = "readOnly")
    @Mapping(target = "diagnosisDate", source = "diagnosisDate")
    DiagnosisResponse toResponse(Diagnosis diagnosis);
}