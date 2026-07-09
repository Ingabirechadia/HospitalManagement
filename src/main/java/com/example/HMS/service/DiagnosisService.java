package com.example.HMS.service;

import com.example.HMS.dto.request.DiagnosisRequest;
import com.example.HMS.dto.response.DiagnosisResponse;
import com.example.HMS.entity.Appointment;
import com.example.HMS.entity.Diagnosis;
import com.example.HMS.entity.Doctor;
import com.example.HMS.enums.AppointmentStatus;
import com.example.HMS.enums.UserRole;
import com.example.HMS.exception.BusinessException;
import com.example.HMS.exception.ResourceNotFoundException;
import com.example.HMS.mapper.DiagnosisMapper;
import com.example.HMS.repository.AppointmentRepository;
import com.example.HMS.repository.DiagnosisRepository;
import com.example.HMS.repository.DoctorRepository;
import com.example.HMS.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final DiagnosisMapper diagnosisMapper;

    @Transactional
    public DiagnosisResponse createDiagnosis(Long appointmentId, DiagnosisRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new BusinessException("Cannot create diagnosis for non-completed appointment");
        }


        if (diagnosisRepository.existsByAppointmentId(appointmentId)) {
            throw new BusinessException("Diagnosis already exists for this appointment");
        }


        var currentUser = SecurityUtils.getCurrentUser();


        Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Doctor profile not found"));

        if (!currentUser.getRole().equals(UserRole.ADMIN) &&
                !appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new BusinessException("Only the assigned doctor can create diagnosis");
        }


        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setAppointment(appointment);
        diagnosis.setDoctor(doctor);
        diagnosis.setSymptoms(request.getSymptoms());
        diagnosis.setDiagnosisNotes(request.getDiagnosisNotes());
        diagnosis.setRecommendedTreatment(request.getRecommendedTreatment());
        diagnosis.setDiagnosisDate(LocalDate.now());
        diagnosis.setReadOnly(false);

        diagnosis = diagnosisRepository.save(diagnosis);

        if (request.getPrescriptionFile() != null && !request.getPrescriptionFile().isEmpty()) {

        }

        return diagnosisMapper.toResponse(diagnosis);
    }

    @Cacheable(value = "diagnoses", key = "#id")
    public DiagnosisResponse getDiagnosisById(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found"));


        var currentUser = SecurityUtils.getCurrentUser();
        if (!currentUser.getRole().equals(UserRole.ADMIN) &&
                !diagnosis.getDoctor().getUser().getId().equals(currentUser.getId()) &&
                !diagnosis.getAppointment().getPatient().getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("You don't have permission to view this diagnosis");
        }

        return diagnosisMapper.toResponse(diagnosis);
    }

    @CacheEvict(value = "diagnoses", key = "#id")
    @Transactional
    public DiagnosisResponse updateDiagnosis(Long id, DiagnosisRequest request) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found"));


        if (diagnosis.isReadOnly()) {
            throw new BusinessException("Cannot modify read-only diagnosis");
        }


        if (!diagnosis.getAppointment().getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new BusinessException("Cannot update diagnosis for non-completed appointment");
        }


        var currentUser = SecurityUtils.getCurrentUser();
        if (!diagnosis.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("Only the assigned doctor can update diagnosis");
        }

        diagnosis.setSymptoms(request.getSymptoms());
        diagnosis.setDiagnosisNotes(request.getDiagnosisNotes());
        diagnosis.setRecommendedTreatment(request.getRecommendedTreatment());

        diagnosis = diagnosisRepository.save(diagnosis);
        return diagnosisMapper.toResponse(diagnosis);
    }
}