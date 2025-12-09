package org.pm.aerologixbackend.controller1;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.TruckRequestDTO;
import org.pm.aerologixbackend.dto.TruckResponseDTO;
import org.pm.aerologixbackend.entity.TruckStatus;
import org.pm.aerologixbackend.service.TruckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    // Get all trucks
    @GetMapping
    public ResponseEntity<List<TruckResponseDTO>> getAllTrucks(
            @RequestParam(required = false) TruckStatus status) {
        if (status != null) {
            return ResponseEntity.ok(truckService.getTrucksByStatus(status));
        }
        return ResponseEntity.ok(truckService.getAllTrucks());
    }

    // Create new truck
    @PostMapping
    public ResponseEntity<TruckResponseDTO> createTruck(
            @Valid @RequestBody TruckRequestDTO request) {
        TruckResponseDTO truck = truckService.createTruck(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(truck);
    }

    // Get truck by ID
    @GetMapping("/{id}")
    public ResponseEntity<TruckResponseDTO> getTruck(@PathVariable Long id) {
        TruckResponseDTO truck = truckService.getTruckById(id);
        return ResponseEntity.ok(truck);
    }

    // Update entire truck (admin operation)
    @PutMapping("/{id}")
    public ResponseEntity<TruckResponseDTO> updateTruck(
            @PathVariable Long id,
            @Valid @RequestBody TruckRequestDTO request) {
        TruckResponseDTO updated = truckService.updateTruck(id, request);
        return ResponseEntity.ok(updated);
    }

    // Update truck status
    @PatchMapping("/{id}/status")
    public ResponseEntity<TruckResponseDTO> updateTruckStatus(
            @PathVariable Long id,
            @RequestParam TruckStatus status) {
        TruckResponseDTO updated = truckService.updateTruckStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // Update truck GPS location (driver operation)
    @PatchMapping("/{id}/location")
    public ResponseEntity<TruckResponseDTO> updateTruckLocation(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        TruckResponseDTO updated = truckService.updateTruckLocation(id, latitude, longitude);
        return ResponseEntity.ok(updated);
    }

    // Delete truck (admin operation)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTruck(@PathVariable Long id) {
        truckService.deleteTruck(id);
        return ResponseEntity.noContent().build();
    }
}