// src/main/java/com/example/HMS/util/SecurityUtils.java

package com.example.HMS.util;

import com.example.HMS.entity.User;
import com.example.HMS.exception.UnauthorizedException;
import com.example.HMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private static final UserRepository userRepository = null;  // Must be 'final'

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("User not found"));
        }

        throw new UnauthorizedException("Invalid authentication");
    }

    public static Long getCurrentUserId() {
        User currentUser = getCurrentUser();
        return currentUser.getId();
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    public boolean isAdmin() {
        User currentUser = getCurrentUser();
        return currentUser.getRole().name().equals("ADMIN");
    }

    public boolean isDoctor() {
        User currentUser = getCurrentUser();
        return currentUser.getRole().name().equals("DOCTOR");
    }

    public boolean isPatient() {
        User currentUser = getCurrentUser();
        return currentUser.getRole().name().equals("PATIENT");
    }
}