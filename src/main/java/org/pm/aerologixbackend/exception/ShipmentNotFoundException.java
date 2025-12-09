package org.pm.aerologixbackend.exception;

public class ShipmentNotFoundException extends RuntimeException {
    public ShipmentNotFoundException(Long id) {
        super("Shipment not found with id: " + id);
    }

    public ShipmentNotFoundException(String trackingNumber) {
        super("Shipment not found with tracking number: " + trackingNumber);
    }
}