package org.pm.aerologixbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.ApiResponse;
import org.pm.aerologixbackend.dto.DriverDTO;
import org.pm.aerologixbackend.entity.DriverStatus;
import org.pm.aerologixbackend.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverDTO.Response>>> getAllDrivers() {
        List<DriverDTO.Response> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverDTO.Response>> getDriverById(@PathVariable Long id) {
        DriverDTO.Response driver = driverService.getDriverById(id);
        return ResponseEntity.ok(ApiResponse.success(driver));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<DriverDTO.Response>> getDriverByEmail(@PathVariable String email) {
        DriverDTO.Response driver = driverService.getDriverByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(driver));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DriverDTO.Response>>> getAvailableDrivers() {
        List<DriverDTO.Response> drivers = driverService.getAvailableDrivers();
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DriverDTO.Response>>> getDriversByStatus(
            @PathVariable DriverStatus status) {
        List<DriverDTO.Response> drivers = driverService.getDriversByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverDTO.Response>> createDriver(
            @Valid @RequestBody DriverDTO.CreateRequest request) {
        DriverDTO.Response driver = driverService.createDriver(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(driver, "Driver added successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverDTO.Response>> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverDTO.UpdateRequest request) {
        DriverDTO.Response driver = driverService.updateDriver(id, request);
        return ResponseEntity.ok(ApiResponse.success(driver, "Driver updated successfully"));
    }

    @PostMapping("/{id}/assign-truck")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverDTO.Response>> assignTruck(
            @PathVariable Long id,
            @RequestBody DriverDTO.AssignTruckRequest request) {
        DriverDTO.Response driver = driverService.assignTruck(id, request.getTruckId());
        return ResponseEntity.ok(ApiResponse.success(driver, "Truck assigned successfully"));
    }

    @PostMapping("/{id}/unassign-truck")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverDTO.Response>> unassignTruck(@PathVariable Long id) {
        DriverDTO.Response driver = driverService.unassignTruck(id);
        return ResponseEntity.ok(ApiResponse.success(driver, "Truck unassigned successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Driver deleted successfully"));
    }
}
