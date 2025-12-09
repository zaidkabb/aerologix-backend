package org.pm.aerologixbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.ApiResponse;
import org.pm.aerologixbackend.dto.TruckDTO;
import org.pm.aerologixbackend.entity.TruckStatus;
import org.pm.aerologixbackend.service.TruckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TruckDTO.Response>>> getAllTrucks() {
        List<TruckDTO.Response> trucks = truckService.getAllTrucks();
        return ResponseEntity.ok(ApiResponse.success(trucks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TruckDTO.Response>> getTruckById(@PathVariable Long id) {
        TruckDTO.Response truck = truckService.getTruckById(id);
        return ResponseEntity.ok(ApiResponse.success(truck));
    }

    @GetMapping("/license/{licensePlate}")
    public ResponseEntity<ApiResponse<TruckDTO.Response>> getTruckByLicensePlate(
            @PathVariable String licensePlate) {
        TruckDTO.Response truck = truckService.getTruckByLicensePlate(licensePlate);
        return ResponseEntity.ok(ApiResponse.success(truck));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<TruckDTO.Response>>> getAvailableTrucks() {
        List<TruckDTO.Response> trucks = truckService.getAvailableTrucks();
        return ResponseEntity.ok(ApiResponse.success(trucks));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TruckDTO.Response>>> getActiveTrucks() {
        List<TruckDTO.Response> trucks = truckService.getActiveTrucks();
        return ResponseEntity.ok(ApiResponse.success(trucks));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TruckDTO.Response>>> getTrucksByStatus(
            @PathVariable TruckStatus status) {
        List<TruckDTO.Response> trucks = truckService.getTrucksByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(trucks));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TruckDTO.Response>> createTruck(
            @Valid @RequestBody TruckDTO.CreateRequest request) {
        TruckDTO.Response truck = truckService.createTruck(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(truck, "Truck added successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TruckDTO.Response>> updateTruck(
            @PathVariable Long id,
            @Valid @RequestBody TruckDTO.UpdateRequest request) {
        TruckDTO.Response truck = truckService.updateTruck(id, request);
        return ResponseEntity.ok(ApiResponse.success(truck, "Truck updated successfully"));
    }

    @PutMapping("/{id}/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<TruckDTO.Response>> updateLocation(
            @PathVariable Long id,
            @RequestBody TruckDTO.LocationUpdate request) {
        TruckDTO.Response truck = truckService.updateLocation(id, request);
        return ResponseEntity.ok(ApiResponse.success(truck, "Location updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTruck(@PathVariable Long id) {
        truckService.deleteTruck(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Truck deleted successfully"));
    }
}
