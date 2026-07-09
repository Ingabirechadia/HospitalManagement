package com.example.HMS.service;

import com.example.HMS.dto.request.HospitalRegistrationRequest;
import com.example.HMS.dto.request.HospitalUpdateRequest;
import com.example.HMS.dto.response.HospitalResponse;
import com.example.HMS.entity.Hospital;
import com.example.HMS.entity.User;
import com.example.HMS.enums.AuthProvider;
import com.example.HMS.enums.UserRole;
import com.example.HMS.exception.BusinessException;
import com.example.HMS.exception.ResourceNotFoundException;
import com.example.HMS.mapper.HospitalMapper;
import com.example.HMS.repository.HospitalRepository;
import com.example.HMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final HospitalMapper hospitalMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public HospitalResponse registerHospital(HospitalRegistrationRequest request) {
        // Validate unique hospital name
        if (hospitalRepository.existsByName(request.getHospitalName())) {
            throw new BusinessException("Hospital name already registered");
        }

        // Validate unique admin email
        if (userRepository.existsByEmail(request.getAdminEmail())) {
            throw new BusinessException("Administrator email already registered");
        }

        // Create admin user
        User admin = new User();
        admin.setEmail(request.getAdminEmail());
        admin.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        admin.setFullName(request.getAdminFullName());
        admin.setPhoneNumber(request.getAdminPhoneNumber());
        admin.setRole(UserRole.ADMIN);
        admin.setAuthProvider(AuthProvider.LOCAL);
        admin.setEnabled(true);
        admin = userRepository.save(admin);

        // Create hospital
        Hospital hospital = new Hospital();
        hospital.setName(request.getHospitalName());
        hospital.setTelephone(request.getTelephone());
        hospital.setPhysicalAddress(request.getPhysicalAddress());
        hospital.setWebsite(request.getWebsite());
        hospital.setAdmin(admin);
        hospital.setActive(true);
        hospital = hospitalRepository.save(hospital);

        return hospitalMapper.toResponse(hospital);
    }

    @Cacheable(value = "hospitals", key = "#id")
    public HospitalResponse getHospitalById(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));
        return hospitalMapper.toResponse(hospital);
    }

    @Cacheable(value = "hospitals", key = "'all'")
    public List<HospitalResponse> getAllHospitals() {
        return hospitalRepository.findAll().stream()
                .map(hospitalMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "hospitals", key = "'active'")
    public List<HospitalResponse> getActiveHospitals() {
        return hospitalRepository.findAllActive().stream()
                .map(hospitalMapper::toResponse)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "hospitals", allEntries = true)
    @Transactional
    public HospitalResponse updateHospital(Long id, HospitalUpdateRequest request) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));

        if (request.getName() != null) {
            if (!request.getName().equals(hospital.getName()) &&
                    hospitalRepository.existsByName(request.getName())) {
                throw new BusinessException("Hospital name already taken");
            }
            hospital.setName(request.getName());
        }

        if (request.getTelephone() != null) {
            hospital.setTelephone(request.getTelephone());
        }
        if (request.getPhysicalAddress() != null) {
            hospital.setPhysicalAddress(request.getPhysicalAddress());
        }
        if (request.getWebsite() != null) {
            hospital.setWebsite(request.getWebsite());
        }
        if (request.getEmail() != null) {
            hospital.setEmail(request.getEmail());
        }
        if (request.getActive() != null) {
            hospital.setActive(request.getActive());
        }

        hospital = hospitalRepository.save(hospital);
        return hospitalMapper.toResponse(hospital);
    }

    @CacheEvict(value = "hospitals", allEntries = true)
    @Transactional
    public void deleteHospital(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));

        // Check if hospital has active doctors
        Long activeDoctors = hospitalRepository.countActiveDoctorsByHospital(id);
        if (activeDoctors > 0) {
            throw new BusinessException("Cannot delete hospital with active doctors. Deactivate doctors first.");
        }

        // Soft delete - deactivate hospital
        hospital.setActive(false);
        hospitalRepository.save(hospital);
    }
}