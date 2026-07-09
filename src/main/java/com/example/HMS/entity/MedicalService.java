// src/main/java/com/example/HMS/entity/MedicalService.java

package com.example.HMS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_services")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MedicalService extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "service_code", unique = true)
    private String serviceCode;

    @Column(name = "base_price")
    private Double basePrice;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    private List<Doctor> doctors = new ArrayList<>();

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    @Column(name = "is_active")
    private boolean active = true;

    // Explicit getters/setters to ensure MapStruct sees properties during annotation processing
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getServiceCode() { return this.serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }

    public Double getBasePrice() { return this.basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Integer getEstimatedDurationMinutes() { return this.estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public Hospital getHospital() { return this.hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public List<Doctor> getDoctors() { return this.doctors; }
    public void setDoctors(List<Doctor> doctors) { this.doctors = doctors; }

    public List<Appointment> getAppointments() { return this.appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }

    public boolean isActive() { return this.active; }
    public void setActive(boolean active) { this.active = active; }
}