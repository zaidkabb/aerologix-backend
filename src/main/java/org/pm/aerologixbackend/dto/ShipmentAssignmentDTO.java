package org.pm.aerologixbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShipmentAssignmentDTO {

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotNull(message = "Truck ID is required")
    private Long truckId;
}