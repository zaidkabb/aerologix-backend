package org.pm.aerologixbackend.service;

import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.ShipmentAssignmentDTO;
import org.pm.aerologixbackend.dto.ShipmentRequestDTO;
import org.pm.aerologixbackend.dto.ShipmentResponseDTO;
import org.pm.aerologixbackend.entity.Driver;
import org.pm.aerologixbackend.entity.DriverStatus;
import org.pm.aerologixbackend.entity.Shipment;
import org.pm.aerologixbackend.entity.ShipmentStatus;
import org.pm.aerologixbackend.entity.Truck;
import org.pm.aerologixbackend.exception.DriverNotFoundException;
import org.pm.aerologixbackend.exception.InvalidDriverException;
import org.pm.aerologixbackend.exception.InvalidShipmentException;
import org.pm.aerologixbackend.exception.ShipmentNotFoundException;
import org.pm.aerologixbackend.exception.TruckNotFoundException;
import org.pm.aerologixbackend.repository.DriverRepository;
import org.pm.aerologixbackend.repository.ShipmentRepository;
import org.pm.aerologixbackend.repository.TruckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;

    private static final double BASE_COST_PER_KM = 0.5;
    private static final double COST_PER_KG = 0.1;

    @Transactional(readOnly = true)
    public List<ShipmentResponseDTO> getAllShipments() {
        return shipmentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShipmentResponseDTO createShipment(ShipmentRequestDTO request) {
        // Business rule: origin and destination cannot be the same
        if (request.getOrigin().equalsIgnoreCase(request.getDestination())) {
            throw new InvalidShipmentException("Origin and destination cannot be the same");
        }

        Shipment shipment = new Shipment();
        shipment.setOrigin(request.getOrigin());
        shipment.setDestination(request.getDestination());
        shipment.setWeight(request.getWeight());

        // Generate unique tracking number
        shipment.setTrackingNumber(generateTrackingNumber());

        // Calculate estimated cost based on distance and weight
        double estimatedDistance = calculateDistance(request.getOrigin(), request.getDestination());
        double cost = calculateCost(estimatedDistance, request.getWeight());
        shipment.setEstimatedCost(cost);

        shipment.setStatus(ShipmentStatus.CREATED);

        Shipment saved = shipmentRepository.save(shipment);
        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public ShipmentResponseDTO getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));
        return toResponseDTO(shipment);
    }

    @Transactional
    public ShipmentResponseDTO updateShipment(Long id, ShipmentRequestDTO request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));

        // Business rule: cannot update delivered or cancelled shipments
        if (shipment.getStatus() == ShipmentStatus.DELIVERED ||
                shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new InvalidShipmentException(
                    "Cannot update shipment with status: " + shipment.getStatus());
        }

        // Business rule: origin and destination cannot be the same
        if (request.getOrigin().equalsIgnoreCase(request.getDestination())) {
            throw new InvalidShipmentException("Origin and destination cannot be the same");
        }

        // Update fields
        shipment.setOrigin(request.getOrigin());
        shipment.setDestination(request.getDestination());
        shipment.setWeight(request.getWeight());

        // Recalculate cost
        double estimatedDistance = calculateDistance(request.getOrigin(), request.getDestination());
        double cost = calculateCost(estimatedDistance, request.getWeight());
        shipment.setEstimatedCost(cost);

        Shipment updated = shipmentRepository.save(shipment);
        return toResponseDTO(updated);
    }

    @Transactional
    public ShipmentResponseDTO updateShipmentStatus(Long id, ShipmentStatus newStatus) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));

        // Business rule: validate status transitions
        validateStatusTransition(shipment.getStatus(), newStatus);

        shipment.setStatus(newStatus);
        Shipment updated = shipmentRepository.save(shipment);
        return toResponseDTO(updated);
    }

    @Transactional
    public void deleteShipment(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));

        // Business rule: cannot delete in-transit or delivered shipments
        if (shipment.getStatus() == ShipmentStatus.IN_TRANSIT ||
                shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new InvalidShipmentException(
                    "Cannot delete shipment with status: " + shipment.getStatus() +
                            ". Only CREATED or CANCELLED shipments can be deleted.");
        }

        shipmentRepository.delete(shipment);
    }

    @Transactional
    public ShipmentResponseDTO assignShipment(Long shipmentId, ShipmentAssignmentDTO assignment) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException(shipmentId));

        Driver driver = driverRepository.findById(assignment.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException(assignment.getDriverId()));

        Truck truck = truckRepository.findById(assignment.getTruckId())
                .orElseThrow(() -> new TruckNotFoundException(assignment.getTruckId()));

        // Business rule: shipment must be in CREATED status
        if (shipment.getStatus() != ShipmentStatus.CREATED) {
            throw new InvalidShipmentException(
                    "Can only assign shipments with CREATED status. Current status: " + shipment.getStatus());
        }

        // Business rule: driver must have this truck assigned
        if (driver.getAssignedTruck() == null || !driver.getAssignedTruck().getId().equals(truck.getId())) {
            throw new InvalidShipmentException(
                    "Driver must have the specified truck assigned. Please assign truck to driver first.");
        }

        // Business rule: driver must be ON_DUTY
        if (driver.getStatus() != DriverStatus.ON_DUTY) {
            throw new InvalidDriverException(
                    "Driver must be ON_DUTY to receive shipment assignments");
        }

        // Assign shipment
        shipment.setAssignedDriver(driver);
        shipment.setAssignedTruck(truck);
        shipment.setAssignedAt(LocalDateTime.now());
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);

        Shipment updated = shipmentRepository.save(shipment);
        return toResponseDTO(updated);
    }

    @Transactional
    public ShipmentResponseDTO unassignShipment(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException(shipmentId));

        // Business rule: can only unassign if not delivered
        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new InvalidShipmentException("Cannot unassign delivered shipment");
        }

        shipment.setAssignedDriver(null);
        shipment.setAssignedTruck(null);
        shipment.setAssignedAt(null);
        shipment.setStatus(ShipmentStatus.CREATED);

        Shipment updated = shipmentRepository.save(shipment);
        return toResponseDTO(updated);
    }

    @Transactional
    public ShipmentResponseDTO markAsDelivered(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException(shipmentId));

        // Business rule: must be in transit
        if (shipment.getStatus() != ShipmentStatus.IN_TRANSIT) {
            throw new InvalidShipmentException(
                    "Can only mark IN_TRANSIT shipments as delivered. Current status: " + shipment.getStatus());
        }

        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(LocalDateTime.now());

        Shipment updated = shipmentRepository.save(shipment);
        return toResponseDTO(updated);
    }

    // ========== HELPER METHODS ==========

    // Helper: Generate unique tracking number
    private String generateTrackingNumber() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "AX-" + uuid;
    }

    // Helper: Calculate distance (mock implementation - in real app use Google Maps API)
    private double calculateDistance(String origin, String destination) {
        // Simple mock: return random distance between 50-500 km
        // In production, you'd use Google Maps Distance Matrix API
        return 100 + (Math.random() * 400);
    }

    // Helper: Calculate shipping cost
    private double calculateCost(double distanceKm, double weightKg) {
        double distanceCost = distanceKm * BASE_COST_PER_KM;
        double weightCost = weightKg * COST_PER_KG;
        return Math.round((distanceCost + weightCost) * 100.0) / 100.0; // Round to 2 decimals
    }

    // Helper: Validate status transitions
    private void validateStatusTransition(ShipmentStatus current, ShipmentStatus next) {
        if (current == ShipmentStatus.DELIVERED) {
            throw new InvalidShipmentException("Cannot update status of delivered shipment");
        }

        if (current == ShipmentStatus.CANCELLED) {
            throw new InvalidShipmentException("Cannot update status of cancelled shipment");
        }

        if (current == ShipmentStatus.CREATED && next == ShipmentStatus.DELIVERED) {
            throw new InvalidShipmentException("Shipment must be in transit before delivery");
        }
    }

    // Helper: Convert entity to DTO
    private ShipmentResponseDTO toResponseDTO(Shipment shipment) {
        Long driverId = shipment.getAssignedDriver() != null ? shipment.getAssignedDriver().getId() : null;
        String driverName = shipment.getAssignedDriver() != null ?
                shipment.getAssignedDriver().getFirstName() + " " + shipment.getAssignedDriver().getLastName() : null;
        Long truckId = shipment.getAssignedTruck() != null ? shipment.getAssignedTruck().getId() : null;
        String truckPlate = shipment.getAssignedTruck() != null ? shipment.getAssignedTruck().getLicensePlate() : null;

        ShipmentResponseDTO dto = new ShipmentResponseDTO();
        dto.setId(shipment.getId());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setOrigin(shipment.getOrigin());
        dto.setDestination(shipment.getDestination());
        dto.setWeight(shipment.getWeight());
        dto.setEstimatedCost(shipment.getEstimatedCost());
        dto.setStatus(shipment.getStatus());
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setAssignedDriverId(driverId);
        dto.setAssignedDriverName(driverName);
        dto.setAssignedTruckId(truckId);
        dto.setAssignedTruckLicensePlate(truckPlate);
        dto.setAssignedAt(shipment.getAssignedAt());
        dto.setDeliveredAt(shipment.getDeliveredAt());

        return dto;
    }
}