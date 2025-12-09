package org.pm.aerologixbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pm.aerologixbackend.entity.Driver;
import org.pm.aerologixbackend.entity.DriverStatus;

import java.time.LocalDateTime;

public class DriverDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Phone is required")
        private String phone;

        @NotBlank(message = "License number is required")
        private String licenseNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String name;
        private String email;
        private String phone;
        private String licenseNumber;
        private DriverStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignTruckRequest {
        private Long truckId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String licenseNumber;
        private DriverStatus status;
        private Long assignedTruckId;
        private String assignedTruckPlate;
        private Integer totalDeliveries;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Driver driver) {
            return Response.builder()
                    .id(driver.getId())
                    .name(driver.getName())
                    .email(driver.getEmail())
                    .phone(driver.getPhone())
                    .licenseNumber(driver.getLicenseNumber())
                    .status(driver.getStatus())
                    .assignedTruckId(driver.getAssignedTruck() != null ? driver.getAssignedTruck().getId() : null)
                    .assignedTruckPlate(driver.getAssignedTruck() != null ? driver.getAssignedTruck().getLicensePlate() : null)
                    .totalDeliveries(driver.getTotalDeliveries())
                    .createdAt(driver.getCreatedAt())
                    .updatedAt(driver.getUpdatedAt())
                    .build();
        }
    }
}
