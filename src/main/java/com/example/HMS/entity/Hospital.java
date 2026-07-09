// src/main/java/com/example/HMS/entity/Hospital.java

package com.example.HMS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospitals")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Hospital extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String telephone;

    @Column(name = "physical_address", columnDefinition = "TEXT")
    private String physicalAddress;

    private String website;

    @Column(name = "registration_number")
    private String registrationNumber;

    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalService> services = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", fetch = FetchType.LAZY)
    private List<Doctor> doctors = new ArrayList<>();

    @Column(name = "is_active")
    private boolean active = true;
}