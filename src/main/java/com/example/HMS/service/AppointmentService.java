package com.example.HMS.service;

import com.example.HMS.dto.request.AppointmentRequest;
import com.example.HMS.dto.response.AppointmentResponse;
import com.example.HMS.entity.Appointment;
import com.example.HMS.entity.Doctor;
import com.example.HMS.entity.MedicalService;
import com.example.HMS.entity.Patient;
import com.example.HMS.enums.AppointmentStatus;
import com.example.HMS.enums.UserRole;
import com.example.HMS.exception.BusinessException;
import com.example.HMS.exception.ResourceNotFoundException;
import com.example.HMS.mapper.AppointmentMapper;
import com.example.HMS.repository.AppointmentRepository;
import com.example.HMS.repository.DoctorRepository;
import com.example.HMS.repository.MedicalServiceRepository;
import com.example.HMS.repository.PatientRepository;
import com.example.HMS.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private static final int MAX_APPOINTMENTS_PER_DAY = 4;

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {

        var currentUser = SecurityUtils.getCurrentUser();


        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Patient profile not found"));


        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (!doctor.isActive()) {
            throw new BusinessException("Doctor is not active");
        }


        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));


        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Appointment date cannot be in the past");
        }

        if (appointmentRepository.existsByPatientAndDateAndTime(
                patient.getId(), request.getAppointmentDate(), request.getAppointmentTime())) {
            throw new BusinessException("You already have an appointment at this time");
        }

        if (appointmentRepository.existsByDoctorAndDateAndTime(
                doctor.getId(), request.getAppointmentDate(), request.getAppointmentTime())) {
            throw new BusinessException("Doctor is already booked at this time");
        }


        Long dailyAppointments = appointmentRepository.countActiveAppointmentsByDoctorAndDate(
                doctor.getId(), request.getAppointmentDate());

        if (dailyAppointments >= MAX_APPOINTMENTS_PER_DAY) {
            throw new BusinessException("Doctor has reached maximum appointments for this day");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setService(service);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setSymptoms(request.getSymptoms());
        appointment.setReasonForVisit(request.getReasonForVisit());
        appointment.setStatus(AppointmentStatus.PENDING);

        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    @Cacheable(value = "appointments", key = "#id")
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));


        var currentUser = SecurityUtils.getCurrentUser();
        if (!currentUser.getRole().equals(UserRole.ADMIN) &&
                !appointment.getPatient().getUser().getId().equals(currentUser.getId()) &&
                !appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("You don't have permission to view this appointment");
        }

        return appointmentMapper.toResponse(appointment);
    }

    @Cacheable(value = "appointments", key = "'patient_' + #patientId")
    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        var currentUser = SecurityUtils.getCurrentUser();


        if (!currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getId().equals(patientId)) {
            throw new BusinessException("You can only view your own appointments");
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "appointments", key = "'doctor_' + #doctorId")
    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        var currentUser = SecurityUtils.getCurrentUser();


        if (!currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getId().equals(doctorId)) {
            throw new BusinessException("You can only view your own appointments");
        }

        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        return appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(value = "appointments", key = "#id"),
            @CacheEvict(value = "appointments", allEntries = true)
    })
    @Transactional
    public AppointmentResponse approveAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getStatus().equals(AppointmentStatus.PENDING)) {
            throw new BusinessException("Only pending appointments can be approved");
        }

        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    @Caching(evict = {
            @CacheEvict(value = "appointments", key = "#id"),
            @CacheEvict(value = "appointments", allEntries = true)
    })
    @Transactional
    public AppointmentResponse rejectAppointment(Long id, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getStatus().equals(AppointmentStatus.PENDING)) {
            throw new BusinessException("Only pending appointments can be rejected");
        }

        appointment.setStatus(AppointmentStatus.REJECTED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledAt(LocalDate.now());
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    @Caching(evict = {
            @CacheEvict(value = "appointments", key = "#id"),
            @CacheEvict(value = "appointments", allEntries = true)
    })
    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        var currentUser = SecurityUtils.getCurrentUser();


        if (!currentUser.getRole().equals(UserRole.ADMIN) &&
                !appointment.getPatient().getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("You can only cancel your own appointments");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Cannot cancel past appointments");
        }

        if (appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new BusinessException("Cannot cancel completed appointments");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDate.now());
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    @Caching(evict = {
            @CacheEvict(value = "appointments", key = "#id"),
            @CacheEvict(value = "appointments", allEntries = true)
    })
    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getStatus().equals(AppointmentStatus.APPROVED)) {
            throw new BusinessException("Only approved appointments can be completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }
}