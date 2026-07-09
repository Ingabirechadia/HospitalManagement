// src/main/java/com/example/HMS/mapper/DoctorMapper.java

package com.example.HMS.mapper;

import com.example.HMS.dto.request.DoctorCreationRequest;
import com.example.HMS.dto.request.UpdateDoctorRequest;
import com.example.HMS.dto.response.DoctorResponse;
import com.example.HMS.entity.Doctor;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hospital", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "diagnoses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "subSpecialization", ignore = true)
    @Mapping(target = "yearsOfExperience", ignore = true)
    @Mapping(target = "qualifications", ignore = true)
    @Mapping(target = "workingHours", ignore = true)
    @Mapping(target = "availableDays", ignore = true)
    @Mapping(target = "active", constant = "true")
    Doctor toEntity(DoctorCreationRequest request);

    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phoneNumber", source = "user.phoneNumber")
    @Mapping(target = "hospitalName", source = "hospital.name")
    @Mapping(target = "hospitalId", source = "hospital.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "medicalLicenseNumber", source = "medicalLicenseNumber")
    @Mapping(target = "specialization", source = "specialization")
    @Mapping(target = "availabilityStatus", source = "availabilityStatus")
    @Mapping(target = "consultationFee", source = "consultationFee")
    @Mapping(target = "services", source = "services")
    @Mapping(target = "active", source = "active")
    DoctorResponse toResponse(Doctor doctor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hospital", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "diagnoses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateDoctorFromRequest(UpdateDoctorRequest request, @MappingTarget Doctor doctor);
}