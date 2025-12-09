package org.pm.aerologixbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    private long totalShipments;
    private long activeShipments;
    private long deliveredShipments;
    private long pendingShipments;

    private long totalTrucks;
    private long availableTrucks;
    private long trucksInUse;
    private long trucksInMaintenance;

    private long totalDrivers;
    private long availableDrivers;
    private long driversOnDelivery;
    private long driversOffDuty;

    private long totalWarehouses;
    private Long totalWarehouseCapacity;
    private Long totalInventory;

    private double deliverySuccessRate;
    private double averageDeliveryTime;

    private List<WeeklyDeliveryData> weeklyDeliveries;
    private List<ShipmentDTO.Response> recentShipments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeeklyDeliveryData {
        private String day;
        private int deliveries;
        private int pending;
    }
}
