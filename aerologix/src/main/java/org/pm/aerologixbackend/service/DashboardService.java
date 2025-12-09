package org.pm.aerologixbackend.service;

import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.DashboardDTO;
import org.pm.aerologixbackend.dto.ShipmentDTO;
import org.pm.aerologixbackend.entity.DriverStatus;
import org.pm.aerologixbackend.entity.ShipmentStatus;
import org.pm.aerologixbackend.entity.TruckStatus;
import org.pm.aerologixbackend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ShipmentRepository shipmentRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;
    private final WarehouseRepository warehouseRepository;

    public DashboardDTO getDashboardStats() {
        // Shipment stats
        long totalShipments = shipmentRepository.count();
        long pendingShipments = shipmentRepository.countByStatus(ShipmentStatus.PENDING);
        long activeShipments = shipmentRepository.countActiveShipments();
        long deliveredShipments = shipmentRepository.countByStatus(ShipmentStatus.DELIVERED);

        // Truck stats
        long totalTrucks = truckRepository.count();
        long availableTrucks = truckRepository.countByStatus(TruckStatus.AVAILABLE);
        long trucksInUse = truckRepository.countByStatus(TruckStatus.IN_USE);
        long trucksInMaintenance = truckRepository.countByStatus(TruckStatus.MAINTENANCE);

        // Driver stats
        long totalDrivers = driverRepository.count();
        long availableDrivers = driverRepository.countByStatus(DriverStatus.AVAILABLE);
        long driversOnDelivery = driverRepository.countByStatus(DriverStatus.ON_DELIVERY);
        long driversOffDuty = driverRepository.countByStatus(DriverStatus.OFF_DUTY);

        // Warehouse stats
        long totalWarehouses = warehouseRepository.count();
        Long totalWarehouseCapacity = warehouseRepository.getTotalCapacity();
        Long totalInventory = warehouseRepository.getTotalInventory();

        // Calculate delivery success rate
        double deliverySuccessRate = totalShipments > 0 ? 
                (deliveredShipments * 100.0) / totalShipments : 0;

        // Get recent shipments
        List<ShipmentDTO.Response> recentShipments = shipmentRepository
                .findRecentShipments(LocalDateTime.now().minusDays(7))
                .stream()
                .limit(5)
                .map(ShipmentDTO.Response::fromEntity)
                .collect(Collectors.toList());

        // Generate weekly delivery data (mock for now)
        List<DashboardDTO.WeeklyDeliveryData> weeklyDeliveries = generateWeeklyData();

        return DashboardDTO.builder()
                .totalShipments(totalShipments)
                .activeShipments(activeShipments)
                .deliveredShipments(deliveredShipments)
                .pendingShipments(pendingShipments)
                .totalTrucks(totalTrucks)
                .availableTrucks(availableTrucks)
                .trucksInUse(trucksInUse)
                .trucksInMaintenance(trucksInMaintenance)
                .totalDrivers(totalDrivers)
                .availableDrivers(availableDrivers)
                .driversOnDelivery(driversOnDelivery)
                .driversOffDuty(driversOffDuty)
                .totalWarehouses(totalWarehouses)
                .totalWarehouseCapacity(totalWarehouseCapacity != null ? totalWarehouseCapacity : 0L)
                .totalInventory(totalInventory != null ? totalInventory : 0L)
                .deliverySuccessRate(Math.round(deliverySuccessRate * 10) / 10.0)
                .averageDeliveryTime(2.4) // Mock value
                .weeklyDeliveries(weeklyDeliveries)
                .recentShipments(recentShipments)
                .build();
    }

    private List<DashboardDTO.WeeklyDeliveryData> generateWeeklyData() {
        List<DashboardDTO.WeeklyDeliveryData> data = new ArrayList<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Random random = new Random();

        for (String day : days) {
            data.add(DashboardDTO.WeeklyDeliveryData.builder()
                    .day(day)
                    .deliveries(random.nextInt(30) + 15)
                    .pending(random.nextInt(12) + 3)
                    .build());
        }

        return data;
    }
}
