package org.pm.aerologixbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pm.aerologixbackend.entity.Warehouse;

import java.time.LocalDateTime;

public class WarehouseDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Location is required")
        private String location;

        @NotBlank(message = "Address is required")
        private String address;

        @Positive(message = "Capacity must be positive")
        private Long capacity;

        @NotBlank(message = "Manager name is required")
        private String manager;

        @NotBlank(message = "Phone is required")
        private String phone;

        private Double latitude;
        private Double longitude;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String name;
        private String location;
        private String address;
        private Long capacity;
        private String manager;
        private String phone;
        private Double latitude;
        private Double longitude;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InventoryUpdateRequest {
        private Long changeAmount; // Positive to add, negative to remove
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String location;
        private String address;
        private Long capacity;
        private Long currentInventory;
        private Integer capacityUsagePercentage;
        private String manager;
        private String phone;
        private Double latitude;
        private Double longitude;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Warehouse warehouse) {
            return Response.builder()
                    .id(warehouse.getId())
                    .name(warehouse.getName())
                    .location(warehouse.getLocation())
                    .address(warehouse.getAddress())
                    .capacity(warehouse.getCapacity())
                    .currentInventory(warehouse.getCurrentInventory())
                    .capacityUsagePercentage(warehouse.getCapacityUsagePercentage())
                    .manager(warehouse.getManager())
                    .phone(warehouse.getPhone())
                    .latitude(warehouse.getLatitude())
                    .longitude(warehouse.getLongitude())
                    .createdAt(warehouse.getCreatedAt())
                    .updatedAt(warehouse.getUpdatedAt())
                    .build();
        }
    }
}
