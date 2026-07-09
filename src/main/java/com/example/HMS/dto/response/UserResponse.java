// src/main/java/com/example/HMS/dto/response/UserResponse.java

package com.example.HMS.dto.response;

import com.example.HMS.enums.AuthProvider;
import com.example.HMS.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String profilePicture;
    private UserRole role;
    private AuthProvider authProvider;
    private Boolean enabled;
    private Boolean locked;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}