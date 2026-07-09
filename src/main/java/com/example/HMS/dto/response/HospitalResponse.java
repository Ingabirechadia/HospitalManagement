// src/main/java/com/example/HMS/dto/response/HospitalResponse.java

package com.example.HMS.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalResponse {
    private Long id;
    private String name;
    private String telephone;
    private String physicalAddress;
    private String adminFullName;
    private String adminEmail;
    private Boolean active;
    private LocalDateTime createdAt;
}