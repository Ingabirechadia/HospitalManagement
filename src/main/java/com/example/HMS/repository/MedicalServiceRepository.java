// src/main/java/com/example/HMS/repository/MedicalServiceRepository.java

package com.example.HMS.repository;

import com.example.HMS.entity.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

    List<MedicalService> findByHospitalId(Long hospitalId);

    @Query("SELECT ms FROM MedicalService ms WHERE ms.hospital.id = :hospitalId AND ms.active = true")
    List<MedicalService> findActiveByHospitalId(@Param("hospitalId") Long hospitalId);

    Optional<MedicalService> findByHospitalIdAndName(Long hospitalId, String name);

    boolean existsByHospitalIdAndName(Long hospitalId, String name);

    @Query("SELECT ms FROM MedicalService ms WHERE ms.hospital.id = :hospitalId ORDER BY ms.name")
    List<MedicalService> findByHospitalIdOrderByName(@Param("hospitalId") Long hospitalId);

    @Query("SELECT ms FROM MedicalService ms JOIN ms.doctors d WHERE d.id = :doctorId")
    List<MedicalService> findByDoctorId(@Param("doctorId") Long doctorId);

    Optional<MedicalService> findByServiceCode(String serviceCode);

    boolean existsByServiceCode(String serviceCode);
}