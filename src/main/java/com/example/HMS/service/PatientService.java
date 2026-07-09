package com.example.HMS.service;

import com.example.HMS.dto.request.PatchPatientRequest;
import com.example.HMS.dto.request.PatientRegistrationRequest;
import com.example.HMS.dto.request.UpdatePatientRequest;
import com.example.HMS.dto.response.PatientResponse;
import com.example.HMS.entity.Patient;
import com.example.HMS.entity.User;
import com.example.HMS.enums.AppointmentStatus;
import com.example.HMS.enums.AuthProvider;
import com.example.HMS.enums.UserRole;
import com.example.HMS.exception.BusinessException;
import com.example.HMS.exception.ResourceNotFoundException;
import com.example.HMS.mapper.PatientMapper;
import com.example.HMS.repository.PatientRepository;
import com.example.HMS.repository.UserRepository;
import com.example.HMS.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper patientMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PatientResponse registerPatient(PatientRegistrationRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        if (request.getNationalId() != null && patientRepository.existsByNationalId(request.getNationalId())) {
            throw new BusinessException("National ID already registered");
        }


        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(UserRole.PATIENT);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setEnabled(true);
        user = userRepository.save(user);


        Patient patient = patientMapper.toEntity(request);
        patient.setUser(user);
        patient = patientRepository.save(patient);

        return patientMapper.toResponse(patient);
    }

    @Cacheable(value = "patients", key = "#id")
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return patientMapper.toResponse(patient);
    }

    @Cacheable(value = "patients", key = "'profile_' + #userId")
    public PatientResponse getPatientProfile(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user: " + userId));
        return patientMapper.toResponse(patient);
    }

    @Caching(evict = {
            @CacheEvict(value = "patients", key = "#id"),
            @CacheEvict(value = "patients", key = "'profile_' + #userId")
    })
    @Transactional
    public PatientResponse updatePatient(Long id, UpdatePatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));


        User currentUser = SecurityUtils.getCurrentUser();
        if (!currentUser.getRole().equals(UserRole.ADMIN) && !patient.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("You can only update your own profile");
        }


        patientMapper.updatePatientFromRequest(request, patient);


        User user = patient.getUser();
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Email already taken");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        user = userRepository.save(user);
        patient.setUser(user);
        patient = patientRepository.save(patient);

        return patientMapper.toResponse(patient);
    }

    @Caching(evict = {
            @CacheEvict(value = "patients", key = "#id"),
            @CacheEvict(value = "patients", key = "'profile_' + #userId")
    })
    @Transactional
    public PatientResponse patchPatient(Long id, PatchPatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));


        User currentUser = SecurityUtils.getCurrentUser();
        if (!currentUser.getRole().equals(UserRole.ADMIN) && !patient.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("You can only update your own profile");
        }

        if (request.getAddress() != null) {
            patient.setAddress(request.getAddress());
        }
        if (request.getPhoneNumber() != null) {
            patient.getUser().setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmergencyContactName() != null) {
            patient.setEmergencyContactName(request.getEmergencyContactName());
        }
        if (request.getEmergencyContactPhone() != null) {
            patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }

        patient = patientRepository.save(patient);
        return patientMapper.toResponse(patient);
    }

    @Caching(evict = {
            @CacheEvict(value = "patients", key = "#id"),
            @CacheEvict(value = "patients", key = "'profile_' + #userId")
    })
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));


        User currentUser = SecurityUtils.getCurrentUser();
        if (!currentUser.getRole().equals(UserRole.ADMIN) && !patient.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("You can only delete your own profile");
        }


        boolean hasActiveAppointments = patient.getAppointments().stream()
                .anyMatch(a -> a.getAppointmentDate().isAfter(LocalDate.now()) &&
                        (a.getStatus().equals(AppointmentStatus.PENDING) ||
                                a.getStatus().equals(AppointmentStatus.APPROVED)));

        if (hasActiveAppointments) {
            throw new BusinessException("Cannot delete account with pending or approved future appointments");
        }


        User user = patient.getUser();
        user.setEnabled(false);
        userRepository.save(user);
    }
}