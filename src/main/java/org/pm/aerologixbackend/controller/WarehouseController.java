package org.pm.aerologixbackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.WarehouseRequestDTO;
import org.pm.aerologixbackend.dto.WarehouseResponseDTO;
import org.pm.aerologixbackend.entity.WarehouseStatus;
import org.pm.aerologixbackend.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    // Get all warehouses
    @GetMapping
    public ResponseEntity<List<WarehouseResponseDTO>> getAllWarehouses(
            @RequestParam(required = false) WarehouseStatus status,
            @RequestParam(required = false) String city) {
        if (status != null) {
            return ResponseEntity.ok(warehouseService.getWarehousesByStatus(status));
        }
        if (city != null) {
            return ResponseEntity.ok(warehouseService.getWarehousesByCity(city));
        }
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    // Create new warehouse
    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> createWarehouse(
            @Valid @RequestBody WarehouseRequestDTO request) {
        WarehouseResponseDTO warehouse = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouse);
    }

    // Get warehouse by ID
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> getWarehouse(@PathVariable Long id) {
        WarehouseResponseDTO warehouse = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(warehouse);
    }

    // Update warehouse details (admin operation)
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequestDTO request) {
        WarehouseResponseDTO updated = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(updated);
    }

    // Update warehouse status
    @PatchMapping("/{id}/status")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouseStatus(
            @PathVariable Long id,
            @RequestParam WarehouseStatus status) {
        WarehouseResponseDTO updated = warehouseService.updateWarehouseStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // Add inventory to warehouse
    @PostMapping("/{id}/inventory/add")
    public ResponseEntity<WarehouseResponseDTO> addInventory(
            @PathVariable Long id,
            @RequestParam Double volume) {
        WarehouseResponseDTO updated = warehouseService.addInventory(id, volume);
        return ResponseEntity.ok(updated);
    }

    // Remove inventory from warehouse
    @PostMapping("/{id}/inventory/remove")
    public ResponseEntity<WarehouseResponseDTO> removeInventory(
            @PathVariable Long id,
            @RequestParam Double volume) {
        WarehouseResponseDTO updated = warehouseService.removeInventory(id, volume);
        return ResponseEntity.ok(updated);
    }

    // Delete warehouse (admin operation)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}