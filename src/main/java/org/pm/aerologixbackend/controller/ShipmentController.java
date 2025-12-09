package org.pm.aerologixbackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.ShipmentAssignmentDTO;
import org.pm.aerologixbackend.dto.ShipmentRequestDTO;
import org.pm.aerologixbackend.dto.ShipmentResponseDTO;
import org.pm.aerologixbackend.entity.ShipmentStatus;
import org.pm.aerologixbackend.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    // Get all shipments
    @GetMapping
    public ResponseEntity<List<ShipmentResponseDTO>> getAllShipments() {
        List<ShipmentResponseDTO> shipments = shipmentService.getAllShipments();
        return ResponseEntity.ok(shipments);
    }

    // Create new shipment
    @PostMapping
    public ResponseEntity<ShipmentResponseDTO> createShipment(
            @Valid @RequestBody ShipmentRequestDTO request) {
        ShipmentResponseDTO shipment = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
    }

    // Assign shipment to driver and truck
    @PostMapping("/{id}/assign")
    public ResponseEntity<ShipmentResponseDTO> assignShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentAssignmentDTO assignment) {
        ShipmentResponseDTO updated = shipmentService.assignShipment(id, assignment);
        return ResponseEntity.ok(updated);
    }

    // Unassign shipment from driver and truck
    @DeleteMapping("/{id}/unassign")
    public ResponseEntity<ShipmentResponseDTO> unassignShipment(@PathVariable Long id) {
        ShipmentResponseDTO updated = shipmentService.unassignShipment(id);
        return ResponseEntity.ok(updated);
    }

    // Mark shipment as delivered
    @PostMapping("/{id}/deliver")
    public ResponseEntity<ShipmentResponseDTO> markAsDelivered(@PathVariable Long id) {
        ShipmentResponseDTO updated = shipmentService.markAsDelivered(id);
        return ResponseEntity.ok(updated);
    }

    // Get shipment by ID
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDTO> getShipment(@PathVariable Long id) {
        ShipmentResponseDTO shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(shipment);
    }

    // Update entire shipment (admin operation)
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentResponseDTO> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentRequestDTO request) {
        ShipmentResponseDTO updated = shipmentService.updateShipment(id, request);
        return ResponseEntity.ok(updated);
    }

    // Update only status (driver operation)
    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentResponseDTO> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam ShipmentStatus status) {
        ShipmentResponseDTO updated = shipmentService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // Delete shipment (admin operation)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }
}