package org.pm.aerologixbackend.service;

import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.ShipmentDTO;
import org.pm.aerologixbackend.entity.*;
import org.pm.aerologixbackend.exception.BadRequestException;
import org.pm.aerologixbackend.exception.ResourceNotFoundException;
import org.pm.aerologixbackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final WarehouseRepository warehouseRepository;
    private final ShipmentTimelineRepository timelineRepository;

    private static long shipmentCounter = 0;

    public List<ShipmentDTO.Response> getAllShipments() {
        return shipmentRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(ShipmentDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public ShipmentDTO.Response getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        return ShipmentDTO.Response.fromEntity(shipment);
    }

    public ShipmentDTO.Response getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with tracking number: " + trackingNumber));
        return ShipmentDTO.Response.fromEntity(shipment);
    }

    @Transactional
    public ShipmentDTO.Response createShipment(ShipmentDTO.CreateRequest request) {
        Shipment shipment = Shipment.builder()
                .trackingNumber(generateTrackingNumber())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .weight(request.getWeight())
                .status(ShipmentStatus.PENDING)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerEmail(request.getCustomerEmail())
                .notes(request.getNotes())
                .estimatedDelivery(request.getEstimatedDelivery() != null ? 
                        request.getEstimatedDelivery() : LocalDate.now().plusDays(7))
                .build();

        if (request.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
            shipment.setWarehouse(warehouse);
        }

        shipment = shipmentRepository.save(shipment);

        // Create initial timeline entry
        createTimelineEntry(shipment, ShipmentStatus.PENDING, shipment.getOrigin(), "Shipment created");

        return ShipmentDTO.Response.fromEntity(shipment);
    }

    @Transactional
    public ShipmentDTO.Response updateShipment(Long id, ShipmentDTO.UpdateRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        if (request.getOrigin() != null) shipment.setOrigin(request.getOrigin());
        if (request.getDestination() != null) shipment.setDestination(request.getDestination());
        if (request.getWeight() != null) shipment.setWeight(request.getWeight());
        if (request.getCustomerName() != null) shipment.setCustomerName(request.getCustomerName());
        if (request.getCustomerPhone() != null) shipment.setCustomerPhone(request.getCustomerPhone());
        if (request.getCustomerEmail() != null) shipment.setCustomerEmail(request.getCustomerEmail());
        if (request.getNotes() != null) shipment.setNotes(request.getNotes());
        if (request.getEstimatedDelivery() != null) shipment.setEstimatedDelivery(request.getEstimatedDelivery());

        if (request.getStatus() != null && request.getStatus() != shipment.getStatus()) {
            updateShipmentStatus(shipment, request.getStatus());
        }

        shipment = shipmentRepository.save(shipment);
        return ShipmentDTO.Response.fromEntity(shipment);
    }

    @Transactional
    public ShipmentDTO.Response assignDriver(Long shipmentId, Long driverId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new BadRequestException("Driver is not available");
        }

        shipment.setDriver(driver);
        
        // Also assign truck if driver has one
        if (driver.getAssignedTruck() != null) {
            shipment.setTruck(driver.getAssignedTruck());
        }

        // Update status
        updateShipmentStatus(shipment, ShipmentStatus.IN_TRANSIT);

        // Update driver status
        driver.setStatus(DriverStatus.ON_DELIVERY);
        driverRepository.save(driver);

        shipment = shipmentRepository.save(shipment);
        return ShipmentDTO.Response.fromEntity(shipment);
    }

    @Transactional
    public ShipmentDTO.Response markAsDelivered(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        updateShipmentStatus(shipment, ShipmentStatus.DELIVERED);
        shipment.setActualDelivery(LocalDate.now());

        // Update driver status and increment delivery count
        if (shipment.getDriver() != null) {
            Driver driver = shipment.getDriver();
            driver.setStatus(DriverStatus.AVAILABLE);
            driver.setTotalDeliveries(driver.getTotalDeliveries() + 1);
            driverRepository.save(driver);
        }

        shipment = shipmentRepository.save(shipment);
        return ShipmentDTO.Response.fromEntity(shipment);
    }

    @Transactional
    public void deleteShipment(Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shipment not found with id: " + id);
        }
        shipmentRepository.deleteById(id);
    }

    public List<ShipmentDTO.Response> getShipmentsByDriver(Long driverId) {
        return shipmentRepository.findByDriverId(driverId)
                .stream()
                .map(ShipmentDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShipmentDTO.Response> getActiveShipmentsByDriver(Long driverId) {
        return shipmentRepository.findByDriverIdAndStatusNot(driverId, ShipmentStatus.DELIVERED)
                .stream()
                .map(ShipmentDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public ShipmentDTO.TrackingResponse trackShipment(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        List<ShipmentTimeline> timeline = timelineRepository.findByTrackingNumberOrderByTimestampAsc(trackingNumber);
        
        List<ShipmentDTO.TimelineEntry> timelineEntries = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        
        // Define the status flow
        ShipmentStatus[] statusFlow = {
                ShipmentStatus.PENDING,
                ShipmentStatus.PICKED_UP,
                ShipmentStatus.IN_TRANSIT,
                ShipmentStatus.OUT_FOR_DELIVERY,
                ShipmentStatus.DELIVERED
        };

        for (ShipmentStatus status : statusFlow) {
            ShipmentTimeline entry = timeline.stream()
                    .filter(t -> t.getStatus() == status)
                    .findFirst()
                    .orElse(null);

            boolean completed = entry != null;
            String timestamp = entry != null ? 
                    entry.getTimestamp().format(formatter) : 
                    getExpectedTimestamp(shipment, status);
            String location = entry != null ? 
                    entry.getLocation() : 
                    getExpectedLocation(shipment, status);

            timelineEntries.add(ShipmentDTO.TimelineEntry.builder()
                    .status(status)
                    .location(location)
                    .timestamp(timestamp)
                    .completed(completed)
                    .build());
        }

        return ShipmentDTO.TrackingResponse.builder()
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .estimatedDelivery(shipment.getEstimatedDelivery())
                .weight(shipment.getWeight())
                .timeline(timelineEntries)
                .build();
    }

    // Helper methods
    private String generateTrackingNumber() {
        shipmentCounter++;
        String year = String.valueOf(LocalDate.now().getYear());
        return String.format("FH-%s-%03d", year, shipmentCounter);
    }

    private void updateShipmentStatus(Shipment shipment, ShipmentStatus newStatus) {
        ShipmentStatus oldStatus = shipment.getStatus();
        shipment.setStatus(newStatus);
        
        String location = determineLocation(shipment, newStatus);
        createTimelineEntry(shipment, newStatus, location, "Status changed from " + oldStatus + " to " + newStatus);
    }

    private void createTimelineEntry(Shipment shipment, ShipmentStatus status, String location, String notes) {
        ShipmentTimeline timeline = ShipmentTimeline.builder()
                .shipment(shipment)
                .status(status)
                .location(location)
                .timestamp(LocalDateTime.now())
                .notes(notes)
                .build();
        timelineRepository.save(timeline);
    }

    private String determineLocation(Shipment shipment, ShipmentStatus status) {
        return switch (status) {
            case PENDING -> shipment.getOrigin();
            case PICKED_UP -> shipment.getOrigin() + " (Pickup Location)";
            case IN_TRANSIT -> "In Transit";
            case OUT_FOR_DELIVERY -> shipment.getDestination() + " (Local Hub)";
            case DELIVERED -> shipment.getDestination();
            case CANCELLED -> "Cancelled";
        };
    }

    private String getExpectedTimestamp(Shipment shipment, ShipmentStatus status) {
        if (shipment.getEstimatedDelivery() == null) return "Pending";
        
        return switch (status) {
            case OUT_FOR_DELIVERY -> "Expected " + shipment.getEstimatedDelivery().minusDays(1);
            case DELIVERED -> "Expected " + shipment.getEstimatedDelivery();
            default -> "Pending";
        };
    }

    private String getExpectedLocation(Shipment shipment, ShipmentStatus status) {
        return switch (status) {
            case PENDING -> shipment.getOrigin();
            case PICKED_UP -> shipment.getOrigin();
            case IN_TRANSIT -> "Transit Hub";
            case OUT_FOR_DELIVERY -> shipment.getDestination() + " Distribution Center";
            case DELIVERED -> shipment.getDestination();
            default -> "Unknown";
        };
    }
}
