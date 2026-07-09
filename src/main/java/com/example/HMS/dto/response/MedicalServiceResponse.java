// src/main/java/com/example/HMS/dto/response/MedicalServiceResponse.java

package com.example.HMS.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceResponse {
    private Long id;
    private String name;
    private String description;
    private String serviceCode;
    private Double basePrice;
    private Integer estimatedDurationMinutes;
    private Boolean active;
    private Long hospitalId;
    private String hospitalName;
}