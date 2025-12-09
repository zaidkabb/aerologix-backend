package org.pm.aerologixbackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.DriverRequestDTO;
import org.pm.aerologixbackend.dto.DriverResponseDTO;
import org.pm.aerologixbackend.entity.DriverStatus;
import org.pm.aerologixbackend.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    // Get all drivers
    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers(
            @RequestParam(required = false) DriverStatus status) {
        if (status != null) {
            return ResponseEntity.ok(driverService.getDriversByStatus(status));
        }
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    // Create new driver
    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(
            @Valid @RequestBody DriverRequestDTO request) {
        DriverResponseDTO driver = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(driver);
    }

    // Get driver by ID
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriver(@PathVariable Long id) {
        DriverResponseDTO driver = driverService.getDriverById(id);
        return ResponseEntity.ok(driver);
    }

    // Update driver details (admin operation)
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverRequestDTO request) {
        DriverResponseDTO updated = driverService.updateDriver(id, request);
        return ResponseEntity.ok(updated);
    }

    // Update driver status
    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverResponseDTO> updateDriverStatus(
            @PathVariable Long id,
            @RequestParam DriverStatus status) {
        DriverResponseDTO updated = driverService.updateDriverStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // Assign truck to driver
    @PostMapping("/{driverId}/assign-truck/{truckId}")
    public ResponseEntity<DriverResponseDTO> assignTruck(
            @PathVariable Long driverId,
            @PathVariable Long truckId) {
        DriverResponseDTO updated = driverService.assignTruckToDriver(driverId, truckId);
        return ResponseEntity.ok(updated);
    }

    // Unassign truck from driver
    @DeleteMapping("/{driverId}/unassign-truck")
    public ResponseEntity<DriverResponseDTO> unassignTruck(@PathVariable Long driverId) {
        DriverResponseDTO updated = driverService.unassignTruckFromDriver(driverId);
        return ResponseEntity.ok(updated);
    }

    // Delete driver (admin operation)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}