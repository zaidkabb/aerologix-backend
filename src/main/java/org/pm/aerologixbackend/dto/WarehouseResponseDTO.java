package org.pm.aerologixbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pm.aerologixbackend.entity.WarehouseStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private Double capacity;
    private Double currentOccupancy;
    private Double availableSpace;
    private Integer occupancyPercentage;
    private WarehouseStatus status;
    private LocalDateTime createdAt;
}