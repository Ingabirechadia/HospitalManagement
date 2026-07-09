package com.example.HMS.repository;

import com.example.HMS.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    Optional<Diagnosis> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);

    List<Diagnosis> findByDoctorId(Long doctorId);

    @Query("SELECT d FROM Diagnosis d WHERE d.appointment.patient.id = :patientId")
    List<Diagnosis> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT d FROM Diagnosis d WHERE d.appointment.appointmentDate BETWEEN :startDate AND :endDate")
    List<Diagnosis> findByDateRange(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM Diagnosis d WHERE d.doctor.id = :doctorId AND d.diagnosisDate BETWEEN :startDate AND :endDate")
    List<Diagnosis> findByDoctorAndDateRange(@Param("doctorId") Long doctorId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}