package org.pm.aerologixbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pm.aerologixbackend.entity.ShipmentStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentResponseDTO {
    private Long id;
    private String trackingNumber;
    private String origin;
    private String destination;
    private Double weight;
    private Double estimatedCost;
    private ShipmentStatus status;
    private LocalDateTime createdAt;

    // Assignment info
    private Long assignedDriverId;
    private String assignedDriverName;
    private Long assignedTruckId;
    private String assignedTruckLicensePlate;
    private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;
}