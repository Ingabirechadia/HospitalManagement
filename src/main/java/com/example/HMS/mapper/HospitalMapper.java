// src/main/java/com/example/HMS/mapper/HospitalMapper.java

package com.example.HMS.mapper;

import com.example.HMS.dto.request.HospitalRegistrationRequest;
import com.example.HMS.dto.request.HospitalUpdateRequest;
import com.example.HMS.dto.response.HospitalResponse;
import com.example.HMS.entity.Hospital;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface HospitalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "website", ignore = true)
    @Mapping(target = "registrationNumber", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "active", constant = "true")
    Hospital toEntity(HospitalRegistrationRequest request);

    @Mapping(target = "adminFullName", source = "admin.fullName")
    @Mapping(target = "adminEmail", source = "admin.email")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "telephone", source = "telephone")
    @Mapping(target = "physicalAddress", source = "physicalAddress")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", source = "createdAt")
    HospitalResponse toResponse(Hospital hospital);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateHospitalFromRequest(HospitalUpdateRequest request, @MappingTarget Hospital hospital);
}