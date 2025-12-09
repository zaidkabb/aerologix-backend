package org.pm.aerologixbackend.controller;

import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.ApiResponse;
import org.pm.aerologixbackend.dto.ShipmentDTO;
import org.pm.aerologixbackend.service.ShipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/track")
@RequiredArgsConstructor
public class TrackingController {

    private final ShipmentService shipmentService;

    @GetMapping("/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShipmentDTO.TrackingResponse>> trackShipment(
            @PathVariable String trackingNumber) {
        ShipmentDTO.TrackingResponse tracking = shipmentService.trackShipment(trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(tracking));
    }
}
