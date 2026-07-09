package com.example.HMS.repository;

import com.example.HMS.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByNationalId(String nationalId);

    Optional<Patient> findByUserId(Long userId);

    @Query("SELECT p FROM Patient p JOIN p.user u WHERE u.email = :email")
    Optional<Patient> findByUserEmail(@Param("email") String email);

    boolean existsByNationalId(String nationalId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Patient p JOIN p.user u WHERE u.email = :email")
    boolean existsByUserEmail(@Param("email") String email);

    @Query("SELECT p FROM Patient p WHERE p.user.enabled = true")
    List<Patient> findAllActive();

    @Query("SELECT p FROM Patient p WHERE p.user.enabled = true AND p.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Patient> findPatientsByDateOfBirthRange(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}