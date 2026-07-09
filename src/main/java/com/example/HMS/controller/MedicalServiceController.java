package com.example.HMS.controller;

import com.example.HMS.dto.request.MedicalServiceRequest;
import com.example.HMS.dto.response.MedicalServiceResponse;
import com.example.HMS.service.MedicalServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;

    @PostMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicalServiceResponse> createService(
            @PathVariable Long hospitalId,
            @Valid @RequestBody MedicalServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalServiceService.createService(hospitalId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalServiceResponse> getService(@PathVariable Long id) {
        return ResponseEntity.ok(medicalServiceService.getServiceById(id));
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<MedicalServiceResponse>> getServicesByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(medicalServiceService.getServicesByHospital(hospitalId));
    }

    @GetMapping("/hospital/{hospitalId}/active")
    public ResponseEntity<List<MedicalServiceResponse>> getActiveServicesByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(medicalServiceService.getActiveServicesByHospital(hospitalId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicalServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody MedicalServiceRequest request) {
        return ResponseEntity.ok(medicalServiceService.updateService(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        medicalServiceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}