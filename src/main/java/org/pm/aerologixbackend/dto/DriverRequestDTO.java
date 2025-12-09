package org.pm.aerologixbackend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DriverRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^[A-Z0-9]{8,15}$",
            message = "License number must be 8-15 alphanumeric characters")
    private String licenseNumber;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be 10-15 digits, optionally starting with +")
    private String phoneNumber;
}