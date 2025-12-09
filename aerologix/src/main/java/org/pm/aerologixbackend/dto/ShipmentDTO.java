package org.pm.aerologixbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pm.aerologixbackend.entity.Shipment;
import org.pm.aerologixbackend.entity.ShipmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ShipmentDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "Origin is required")
        private String origin;

        @NotBlank(message = "Destination is required")
        private String destination;

        @Positive(message = "Weight must be positive")
        private Double weight;

        private String customerName;
        private String customerPhone;
        private String customerEmail;
        private String notes;
        private LocalDate estimatedDelivery;
        private Long warehouseId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String origin;
        private String destination;
        private Double weight;
        private ShipmentStatus status;
        private String customerName;
        private String customerPhone;
        private String customerEmail;
        private String notes;
        private LocalDate estimatedDelivery;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignDriverRequest {
        private Long driverId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String trackingNumber;
        private String origin;
        private String destination;
        private ShipmentStatus status;
        private Double weight;
        private Long driverId;
        private String driverName;
        private Long truckId;
        private String truckLicensePlate;
        private String customerName;
        private String customerPhone;
        private String customerEmail;
        private String notes;
        private LocalDate estimatedDelivery;
        private LocalDate actualDelivery;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Shipment shipment) {
            return Response.builder()
                    .id(shipment.getId())
                    .trackingNumber(shipment.getTrackingNumber())
                    .origin(shipment.getOrigin())
                    .destination(shipment.getDestination())
                    .status(shipment.getStatus())
                    .weight(shipment.getWeight())
                    .driverId(shipment.getDriver() != null ? shipment.getDriver().getId() : null)
                    .driverName(shipment.getDriver() != null ? shipment.getDriver().getName() : null)
                    .truckId(shipment.getTruck() != null ? shipment.getTruck().getId() : null)
                    .truckLicensePlate(shipment.getTruck() != null ? shipment.getTruck().getLicensePlate() : null)
                    .customerName(shipment.getCustomerName())
                    .customerPhone(shipment.getCustomerPhone())
                    .customerEmail(shipment.getCustomerEmail())
                    .notes(shipment.getNotes())
                    .estimatedDelivery(shipment.getEstimatedDelivery())
                    .actualDelivery(shipment.getActualDelivery())
                    .createdAt(shipment.getCreatedAt())
                    .updatedAt(shipment.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrackingResponse {
        private String trackingNumber;
        private ShipmentStatus status;
        private String origin;
        private String destination;
        private LocalDate estimatedDelivery;
        private Double weight;
        private List<TimelineEntry> timeline;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimelineEntry {
        private ShipmentStatus status;
        private String location;
        private String timestamp;
        private boolean completed;
    }
}
