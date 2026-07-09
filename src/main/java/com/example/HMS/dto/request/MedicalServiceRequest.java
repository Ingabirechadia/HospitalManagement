package com.example.HMS.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MedicalServiceRequest {

    @NotBlank(message = "Service name is required")
    @Size(min = 2, max = 100, message = "Service name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Service code is required")
    @Pattern(regexp = "^[A-Z0-9]{3,10}$", message = "Service code must be 3-10 alphanumeric characters")
    private String serviceCode;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private Double basePrice;

    @NotNull(message = "Estimated duration is required")
    @Min(value = 5, message = "Minimum duration is 5 minutes")
    @Max(value = 480, message = "Maximum duration is 480 minutes (8 hours)")
    private Integer estimatedDurationMinutes;

    private Boolean active = true;
}