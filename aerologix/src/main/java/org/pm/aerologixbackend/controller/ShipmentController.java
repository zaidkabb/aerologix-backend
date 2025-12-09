package org.pm.aerologixbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.ApiResponse;
import org.pm.aerologixbackend.dto.ShipmentDTO;
import org.pm.aerologixbackend.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipmentDTO.Response>>> getAllShipments() {
        List<ShipmentDTO.Response> shipments = shipmentService.getAllShipments();
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentDTO.Response>> getShipmentById(@PathVariable Long id) {
        ShipmentDTO.Response shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShipmentDTO.Response>> getShipmentByTrackingNumber(
            @PathVariable String trackingNumber) {
        ShipmentDTO.Response shipment = shipmentService.getShipmentByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentDTO.Response>> createShipment(
            @Valid @RequestBody ShipmentDTO.CreateRequest request) {
        ShipmentDTO.Response shipment = shipmentService.createShipment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(shipment, "Shipment created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentDTO.Response>> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentDTO.UpdateRequest request) {
        ShipmentDTO.Response shipment = shipmentService.updateShipment(id, request);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Shipment updated successfully"));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentDTO.Response>> assignDriver(
            @PathVariable Long id,
            @RequestBody ShipmentDTO.AssignDriverRequest request) {
        ShipmentDTO.Response shipment = shipmentService.assignDriver(id, request.getDriverId());
        return ResponseEntity.ok(ApiResponse.success(shipment, "Driver assigned successfully"));
    }

    @PostMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<ShipmentDTO.Response>> markAsDelivered(@PathVariable Long id) {
        ShipmentDTO.Response shipment = shipmentService.markAsDelivered(id);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Shipment marked as delivered"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Shipment deleted successfully"));
    }

    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<List<ShipmentDTO.Response>>> getShipmentsByDriver(
            @PathVariable Long driverId) {
        List<ShipmentDTO.Response> shipments = shipmentService.getShipmentsByDriver(driverId);
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/driver/{driverId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<List<ShipmentDTO.Response>>> getActiveShipmentsByDriver(
            @PathVariable Long driverId) {
        List<ShipmentDTO.Response> shipments = shipmentService.getActiveShipmentsByDriver(driverId);
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }
}
