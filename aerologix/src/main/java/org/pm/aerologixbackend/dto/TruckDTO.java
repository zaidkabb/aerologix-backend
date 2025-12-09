package org.pm.aerologixbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pm.aerologixbackend.entity.Truck;
import org.pm.aerologixbackend.entity.TruckStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TruckDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "License plate is required")
        private String licensePlate;

        @NotBlank(message = "Model is required")
        private String model;

        @Positive(message = "Capacity must be positive")
        private Double capacity;

        private TruckStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String licensePlate;
        private String model;
        private Double capacity;
        private TruckStatus status;
        private Long mileage;
        private LocalDate lastService;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocationUpdate {
        private Double latitude;
        private Double longitude;
        private Double speed;
        private String heading;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String licensePlate;
        private String model;
        private Double capacity;
        private TruckStatus status;
        private Long mileage;
        private LocalDate lastService;
        private LocalDate nextService;
        private Double currentLatitude;
        private Double currentLongitude;
        private Double currentSpeed;
        private String heading;
        private LocalDateTime lastLocationUpdate;
        private Long assignedDriverId;
        private String assignedDriverName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Truck truck) {
            return Response.builder()
                    .id(truck.getId())
                    .licensePlate(truck.getLicensePlate())
                    .model(truck.getModel())
                    .capacity(truck.getCapacity())
                    .status(truck.getStatus())
                    .mileage(truck.getMileage())
                    .lastService(truck.getLastService())
                    .nextService(truck.getNextService())
                    .currentLatitude(truck.getCurrentLatitude())
                    .currentLongitude(truck.getCurrentLongitude())
                    .currentSpeed(truck.getCurrentSpeed())
                    .heading(truck.getHeading())
                    .lastLocationUpdate(truck.getLastLocationUpdate())
                    .assignedDriverId(truck.getAssignedDriver() != null ? truck.getAssignedDriver().getId() : null)
                    .assignedDriverName(truck.getAssignedDriver() != null ? truck.getAssignedDriver().getName() : null)
                    .createdAt(truck.getCreatedAt())
                    .updatedAt(truck.getUpdatedAt())
                    .build();
        }
    }
}
