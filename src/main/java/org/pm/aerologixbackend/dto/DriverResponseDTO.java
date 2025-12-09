package org.pm.aerologixbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pm.aerologixbackend.entity.DriverStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String licenseNumber;
    private String phoneNumber;
    private DriverStatus status;
    private Long assignedTruckId;
    private String assignedTruckLicensePlate;
    private LocalDateTime createdAt;
}