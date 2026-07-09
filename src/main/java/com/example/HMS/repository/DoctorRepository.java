package com.example.HMS.repository;

import com.example.HMS.entity.Doctor;
import com.example.HMS.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByMedicalLicenseNumber(String licenseNumber);

    Optional<Doctor> findByUserId(Long userId);

    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE u.email = :email")
    Optional<Doctor> findByUserEmail(@Param("email") String email);

    boolean existsByMedicalLicenseNumber(String licenseNumber);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Doctor d JOIN d.user u WHERE u.email = :email")
    boolean existsByUserEmail(@Param("email") String email);

    List<Doctor> findByHospitalId(Long hospitalId);

    List<Doctor> findByServicesId(Long serviceId);

    List<Doctor> findByAvailabilityStatus(AvailabilityStatus status);

    @Query("SELECT d FROM Doctor d WHERE d.active = true AND d.hospital.id = :hospitalId")
    List<Doctor> findActiveDoctorsByHospital(@Param("hospitalId") Long hospitalId);

    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.appointments a WHERE a.appointmentDate = :date AND a.status != 'CANCELLED'")
    List<Doctor> findDoctorsWithAppointmentsOnDate(@Param("date") LocalDate date);

    @Query("SELECT d FROM Doctor d WHERE d.active = true AND d.availabilityStatus = :status")
    List<Doctor> findActiveDoctorsByAvailabilityStatus(@Param("status") AvailabilityStatus status);
}