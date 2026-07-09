package com.example.HMS.repository;

import com.example.HMS.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByDiagnosisId(Long diagnosisId);

    @Query("SELECT p FROM Prescription p WHERE p.diagnosis.appointment.patient.id = :patientId")
    List<Prescription> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT p FROM Prescription p WHERE p.diagnosis.doctor.id = :doctorId")
    List<Prescription> findByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT p FROM Prescription p WHERE p.fileName LIKE %:fileName%")
    List<Prescription> findByFileNameContaining(@Param("fileName") String fileName);
}