package com.example.HMS.repository;

import com.example.HMS.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Optional<Hospital> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT h FROM Hospital h WHERE h.active = true")
    List<Hospital> findAllActive();

    @Query("SELECT h FROM Hospital h WHERE h.admin.id = :adminId")
    Optional<Hospital> findByAdminId(@Param("adminId") Long adminId);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.hospital.id = :hospitalId AND d.active = true")
    Long countActiveDoctorsByHospital(@Param("hospitalId") Long hospitalId);
}