package org.pm.aerologixbackend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TruckRequestDTO {

    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "^[A-Z]{1,3}-[A-Z]{1,2}-[0-9]{1,4}$",
            message = "License plate must follow format: AB-CD-1234")
    private String licensePlate;

    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters")
    private String model;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    @Max(value = 50000, message = "Capacity cannot exceed 50000 kg")
    private Double capacity;
}