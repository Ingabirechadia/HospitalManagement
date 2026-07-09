// src/main/java/com/example/HMS/dto/response/PatientResponse.java

package com.example.HMS.dto.response;

import com.example.HMS.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String nationalId;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private LocalDateTime createdAt;
}