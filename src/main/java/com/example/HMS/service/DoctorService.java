package com.example.HMS.service;

import com.example.HMS.dto.request.DoctorCreationRequest;
import com.example.HMS.dto.request.UpdateDoctorRequest;
import com.example.HMS.dto.response.DoctorResponse;
import com.example.HMS.entity.Doctor;
import com.example.HMS.entity.Hospital;
import com.example.HMS.entity.MedicalService;
import com.example.HMS.entity.User;
import com.example.HMS.enums.AppointmentStatus;
import com.example.HMS.enums.AuthProvider;
import com.example.HMS.enums.AvailabilityStatus;
import com.example.HMS.enums.UserRole;
import com.example.HMS.exception.BusinessException;
import com.example.HMS.exception.ResourceNotFoundException;
import com.example.HMS.mapper.DoctorMapper;
import com.example.HMS.repository.DoctorRepository;
import com.example.HMS.repository.HospitalRepository;
import com.example.HMS.repository.MedicalServiceRepository;
import com.example.HMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final DoctorMapper doctorMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DoctorResponse createDoctor(Long hospitalId, DoctorCreationRequest request) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));


        if (doctorRepository.existsByMedicalLicenseNumber(request.getMedicalLicenseNumber())) {
            throw new BusinessException("Medical license number already exists");
        }


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }


        List<MedicalService> services = medicalServiceRepository.findAllById(request.getServiceIds());
        if (services.size() != request.getServiceIds().size()) {
            throw new BusinessException("One or more services not found");
        }


        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode("temp123")); // Temporary password, will be changed
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(UserRole.DOCTOR);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setEnabled(true);
        user = userRepository.save(user);


        Doctor doctor = doctorMapper.toEntity(request);
        doctor.setUser(user);
        doctor.setHospital(hospital);
        doctor.setServices(services);
        doctor = doctorRepository.save(doctor);

        return doctorMapper.toResponse(doctor);
    }

    @Cacheable(value = "doctors", key = "#id")
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return doctorMapper.toResponse(doctor);
    }

    @Cacheable(value = "doctors", key = "'hospital_' + #hospitalId")
    public List<DoctorResponse> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalId(hospitalId).stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "doctors", key = "'service_' + #serviceId")
    public List<DoctorResponse> getDoctorsByService(Long serviceId) {
        return doctorRepository.findByServicesId(serviceId).stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(value = "doctors", key = "#id"),
            @CacheEvict(value = "doctors", allEntries = true)
    })
    @Transactional
    public DoctorResponse updateDoctor(Long id, UpdateDoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));


        User user = doctor.getUser();
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Email already taken");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        userRepository.save(user);


        if (request.getMedicalLicenseNumber() != null) {
            if (!request.getMedicalLicenseNumber().equals(doctor.getMedicalLicenseNumber()) &&
                    doctorRepository.existsByMedicalLicenseNumber(request.getMedicalLicenseNumber())) {
                throw new BusinessException("Medical license number already exists");
            }
            doctor.setMedicalLicenseNumber(request.getMedicalLicenseNumber());
        }
        if (request.getSpecialization() != null) {
            doctor.setSpecialization(request.getSpecialization());
        }
        if (request.getAvailabilityStatus() != null) {
            doctor.setAvailabilityStatus(request.getAvailabilityStatus());
        }
        if (request.getConsultationFee() != null) {
            doctor.setConsultationFee(request.getConsultationFee());
        }
        if (request.getActive() != null) {
            doctor.setActive(request.getActive());
        }


        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            List<MedicalService> services = medicalServiceRepository.findAllById(request.getServiceIds());
            if (services.size() != request.getServiceIds().size()) {
                throw new BusinessException("One or more services not found");
            }
            doctor.setServices(services);
        }

        doctor = doctorRepository.save(doctor);
        return doctorMapper.toResponse(doctor);
    }

    @Caching(evict = {
            @CacheEvict(value = "doctors", key = "#id"),
            @CacheEvict(value = "doctors", allEntries = true)
    })
    @Transactional
    public void deactivateDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));


        boolean hasFutureAppointments = doctor.getAppointments().stream()
                .anyMatch(a -> a.getAppointmentDate().isAfter(LocalDate.now()) &&
                        !a.getStatus().equals(AppointmentStatus.CANCELLED) &&
                        !a.getStatus().equals(AppointmentStatus.COMPLETED));

        if (hasFutureAppointments) {
            throw new BusinessException("Cannot deactivate doctor with future appointments");
        }

        doctor.setActive(false);
        doctor.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);
        doctorRepository.save(doctor);
    }
}