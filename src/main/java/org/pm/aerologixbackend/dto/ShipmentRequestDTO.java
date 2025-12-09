package org.pm.aerologixbackend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ShipmentRequestDTO {

    @NotBlank(message = "Origin is required")
    @Size(min = 2, max = 100, message = "Origin must be between 2 and 100 characters")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(min = 2, max = 100, message = "Destination must be between 2 and 100 characters")
    private String destination;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Max(value = 10000, message = "Weight cannot exceed 10000 kg")
    private Double weight;
}