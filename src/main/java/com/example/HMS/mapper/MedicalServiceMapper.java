// src/main/java/com/example/HMS/mapper/MedicalServiceMapper.java

package com.example.HMS.mapper;

import com.example.HMS.dto.request.MedicalServiceRequest;
import com.example.HMS.dto.response.MedicalServiceResponse;
import com.example.HMS.entity.MedicalService;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MedicalServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hospital", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    MedicalService toEntity(MedicalServiceRequest request);

    @Mapping(target = "hospitalId", source = "hospital.id")
    @Mapping(target = "hospitalName", source = "hospital.name")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "serviceCode", source = "serviceCode")
    @Mapping(target = "basePrice", source = "basePrice")
    @Mapping(target = "estimatedDurationMinutes", source = "estimatedDurationMinutes")
    @Mapping(target = "active", source = "active")
    MedicalServiceResponse toResponse(MedicalService service);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hospital", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateServiceFromRequest(MedicalServiceRequest request, @MappingTarget MedicalService service);
}