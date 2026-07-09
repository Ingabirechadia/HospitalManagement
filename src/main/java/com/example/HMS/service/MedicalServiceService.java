package com.example.HMS.service;

import com.example.HMS.dto.request.MedicalServiceRequest;
import com.example.HMS.dto.response.MedicalServiceResponse;
import com.example.HMS.entity.Hospital;
import com.example.HMS.entity.MedicalService;
import com.example.HMS.exception.BusinessException;
import com.example.HMS.exception.ResourceNotFoundException;
import com.example.HMS.mapper.MedicalServiceMapper;
import com.example.HMS.repository.HospitalRepository;
import com.example.HMS.repository.MedicalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;
    private final HospitalRepository hospitalRepository;
    private final MedicalServiceMapper medicalServiceMapper;

    @Transactional
    public MedicalServiceResponse createService(Long hospitalId, MedicalServiceRequest request) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));

        if (medicalServiceRepository.existsByHospitalIdAndName(hospitalId, request.getName())) {
            throw new BusinessException("Service name already exists in this hospital");
        }


        if (medicalServiceRepository.existsByServiceCode(request.getServiceCode())) {
            throw new BusinessException("Service code already exists");
        }

        MedicalService service = medicalServiceMapper.toEntity(request);
        service.setHospital(hospital);
        service.setActive(true);
        service = medicalServiceRepository.save(service);

        return medicalServiceMapper.toResponse(service);
    }

    @Cacheable(value = "services", key = "#id")
    public MedicalServiceResponse getServiceById(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return medicalServiceMapper.toResponse(service);
    }

    @Cacheable(value = "services", key = "'hospital_' + #hospitalId")
    public List<MedicalServiceResponse> getServicesByHospital(Long hospitalId) {
        return medicalServiceRepository.findByHospitalId(hospitalId).stream()
                .map(medicalServiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "services", key = "'hospital_' + #hospitalId + '_active'")
    public List<MedicalServiceResponse> getActiveServicesByHospital(Long hospitalId) {
        return medicalServiceRepository.findActiveByHospitalId(hospitalId).stream()
                .map(medicalServiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "services", allEntries = true)
    @Transactional
    public MedicalServiceResponse updateService(Long id, MedicalServiceRequest request) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));


        if (request.getName() != null && !request.getName().equals(service.getName())) {
            if (medicalServiceRepository.existsByHospitalIdAndName(
                    service.getHospital().getId(), request.getName())) {
                throw new BusinessException("Service name already exists in this hospital");
            }
            service.setName(request.getName());
        }

        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        if (request.getServiceCode() != null) {
            if (!request.getServiceCode().equals(service.getServiceCode()) &&
                    medicalServiceRepository.existsByServiceCode(request.getServiceCode())) {
                throw new BusinessException("Service code already exists");
            }
            service.setServiceCode(request.getServiceCode());
        }
        if (request.getBasePrice() != null) {
            service.setBasePrice(request.getBasePrice());
        }
        if (request.getEstimatedDurationMinutes() != null) {
            service.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        }

        service = medicalServiceRepository.save(service);
        return medicalServiceMapper.toResponse(service);
    }

    @CacheEvict(value = "services", allEntries = true)
    @Transactional
    public void deleteService(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        if (!service.getAppointments().isEmpty()) {
            throw new BusinessException("Cannot delete service with existing appointments. Deactivate instead.");
        }


        service.setActive(false);
        medicalServiceRepository.save(service);
    }
}