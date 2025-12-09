package org.pm.aerologixbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.ApiResponse;
import org.pm.aerologixbackend.dto.WarehouseDTO;
import org.pm.aerologixbackend.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseDTO.Response>>> getAllWarehouses() {
        List<WarehouseDTO.Response> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDTO.Response>> getWarehouseById(@PathVariable Long id) {
        WarehouseDTO.Response warehouse = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(ApiResponse.success(warehouse));
    }

    @GetMapping("/low-occupancy")
    public ResponseEntity<ApiResponse<List<WarehouseDTO.Response>>> getWarehousesWithLowOccupancy() {
        List<WarehouseDTO.Response> warehouses = warehouseService.getWarehousesWithLowOccupancy();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }

    @GetMapping("/high-occupancy")
    public ResponseEntity<ApiResponse<List<WarehouseDTO.Response>>> getWarehousesWithHighOccupancy() {
        List<WarehouseDTO.Response> warehouses = warehouseService.getWarehousesWithHighOccupancy();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseDTO.Response>> createWarehouse(
            @Valid @RequestBody WarehouseDTO.CreateRequest request) {
        WarehouseDTO.Response warehouse = warehouseService.createWarehouse(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(warehouse, "Warehouse added successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseDTO.Response>> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseDTO.UpdateRequest request) {
        WarehouseDTO.Response warehouse = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(ApiResponse.success(warehouse, "Warehouse updated successfully"));
    }

    @PutMapping("/{id}/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseDTO.Response>> updateInventory(
            @PathVariable Long id,
            @RequestBody WarehouseDTO.InventoryUpdateRequest request) {
        WarehouseDTO.Response warehouse = warehouseService.updateInventory(id, request);
        return ResponseEntity.ok(ApiResponse.success(warehouse, "Inventory updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Warehouse deleted successfully"));
    }
}
