// src/main/java/com/example/HMS/entity/Doctor.java

package com.example.HMS.entity;

import com.example.HMS.enums.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "medical_license_number", nullable = false, unique = true)
    private String medicalLicenseNumber;

    @Column(nullable = false)
    private String specialization;

    @Column(name = "sub_specialization")
    private String subSpecialization;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "qualifications", columnDefinition = "TEXT")
    private String qualifications;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status")
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    @Column(name = "consultation_fee")
    private Double consultationFee;

    @Column(name = "working_hours")
    private String workingHours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_services",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<MedicalService> services = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Diagnosis> diagnoses = new ArrayList<>();

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "available_days")
    private String availableDays;
}