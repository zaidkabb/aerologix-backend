package org.pm.aerologixbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pm.aerologixbackend.entity.TruckStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TruckResponseDTO {
    private Long id;
    private String licensePlate;
    private String model;
    private Double capacity;
    private TruckStatus status;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime createdAt;
}